ADR-0001 — Authentication architecture and build-vs-buy boundary

Status: Proposed · Date: 2026-07-25 · Deciders: Mayank
Related: ADR-0002 (repo & build strategy), ADR-0003 (Passport wire format)

Context and problem statement

The super app will carry three revenue domains — commerce marketplace (buyers + sellers), payments (P2P/P2M), streaming — as independently deployable services over gRPC and Kafka, fronted by Angular. Every domain must answer on every request: who is this, what may they do, and is the calling service itself trustworthy?

Answering all three inside one "auth service" couples an identity provider, a permission engine, and a workload trust root into a single deployable. Netflix, Google and Meta each separate them. The boundary must be fixed before the first line of the identity service, because reversing it later means re-touching every downstream service.

Decision drivers

D1 Must survive commerce → payments → streaming without re-architecture; payments will demand step-up auth and regulator-grade audit trails.
D2 No downstream service may ever parse an externally-issued token.
D3 Hot-path authorization must not add a synchronous network hop per check.
D4 Solo-engineer capacity — build only what teaches durable skill or what no vendor solves well.
D5 Every external-facing choice maps to a published spec, not a blog post.
Decision

Split "auth" into four planes; apply a different build-vs-buy rule to each.

Plane	Concern	Decision	Rationale
P1 AuthN	who is this human/client	BUILD — identity-service: OIDC 1.0 / OAuth 2.1 provider on Spring Boot 4 + Spring Security 7	Core learning goal; spec-bounded so scope cannot creep
P2 AuthZ	may subject S do action A on resource R	ADOPT — OpenFGA or SpiceDB, behind our own thin authz-service façade	Zanzibar-grade consistency is a multi-year build; façade preserves swap-ability
P3 Workload identity	is this caller really order-service	ADOPT, deferred to Phase 4 — SPIFFE/SPIRE + mTLS	Until then: single trust zone, logged as accepted risk R2
P4 Identity propagation	how identity crosses the mesh	BUILD — "Passport", minted only at edge-gateway	The one piece no vendor sells, and the piece that keeps P1 replaceable

Boundary rule: the edge gateway is the only component permitted to accept an externally-issued credential. Everything behind it carries a Passport and nothing else. Any service that reads a Bearer header is a defect.

Passport v1 shape
Minted by edge-gateway after access-token validation. Never accepted from a client — inbound Passport headers are stripped before injection.
Serialization: compact JWS, EdDSA (Ed25519), behind a PassportCodec interface so v2 can move to protobuf without touching callers.
TTL 60s, aud = internal mesh.
Claims: sub, did (device), sid (session), lvl (assurance level 0–3), act (actor/impersonation, null in v1), iat, exp, jti.
Deliberately absent: roles, permissions, entitlements. Those are P2's job; embedding them turns the Passport into a stale cache and re-creates the "very lengthy JWT" problem Netflix warns about.
Token lifetimes and revocation
Token	Format	Lifetime	Rotation
Access	JWT, EdDSA	5 min	—
Refresh	opaque, hashed at rest	30 d sliding	rotate every use; replay of a consumed token revokes the whole family
Passport	JWS	60 s	per-request
Signing keys	Ed25519	90 d	2 keys live in JWKS, overlapping publish

Revocation: logout / password change / fraud signal publishes to Kafka identity.session.revoked.v1; edge-gateway holds an in-memory revoked-sid set. The 5-minute access TTL bounds worst-case staleness, so we accept eventual consistency instead of synchronous introspection (D3).

Explicit non-goals for v1

Social/federated login · SAML · multi-tenancy and B2B org hierarchies · passkeys/WebAuthn (Phase 5) · risk-based adaptive auth (Phase 5) · third-party clients and consent screens (first-party only in v1) · account recovery beyond email OTP · SCIM.

Each of these is a decision, not an oversight. That distinction is the whole point of writing it down.

Consequences

Positive: downstream services compile with zero OAuth dependencies; P1 can be swapped for Keycloak in a weekend if R1 fires; payments can raise lvl for step-up without any protocol change.

Risks

R1 — a hand-built IdP is a security liability until proven. Mitigation: pass the OpenID Foundation conformance suite before real user data touches it.
R2 — no workload identity until Phase 4. Mitigation: single trust zone, no untrusted workloads; revisit in a later ADR.
R3 — v1 key management is env/filesystem-based. Mitigation: KeyStore interface from day 1, swap to Vault/KMS in Phase 5.
R4 — Spring Security 7 is a young generation and nearly all spring-authorization-server 1.x tutorials are now stale. Mitigation: reference docs only.
Alternatives considered
Adopt Keycloak for P1. Mature, certified, ships MFA/DPoP/federation free. Rejected for v1 — defeats D4. But run it locally as an oracle to diff your behaviour against.
One monolithic auth-service (P1+P2 fused). Rejected — issuance and permission checks have different scaling and consistency profiles, and it drags authz data into the identity DB.
Propagate the original JWT downstream. Rejected — this is exactly the brittleness Netflix re-architected away from.
Opaque tokens + per-request introspection. Rejected — violates D3.
Validation

Accepted as validated when: (a) the discovery document passes OpenID conformance basic profile, (b) a downstream service builds with no OAuth library on its classpath, (c) revoking a session halts requests at the gateway within one access-token TTL.

Where this file lives

Monorepo, single Git repo. Polyrepo for a solo builder means you version 12 things to change one gRPC field. Split later when you have teams, not before.

superapp/
├── docs/
│   ├── decisions/                 ← ADRs live here
│   │   ├── README.md              (the decision log / index)
│   │   ├── adr-template.md
│   │   └── 0001-auth-architecture-and-build-vs-buy.md   ← this file
│   ├── architecture/{c4,threat-models,diagrams}/
│   ├── runbooks/
│   └── journal/                   (daily engineering journal)
├── contracts/                     ← single source of truth, owned by no service
│   ├── proto/                     (gRPC)
│   ├── openapi/                   (edge-facing REST)
│   └── asyncapi/                  (Kafka event schemas)
├── services/
│   ├── edge-gateway/
│   ├── identity-service/          ← Phase 1 starts here
│   ├── authz-service/
│   └── ...
├── libs/
│   ├── passport-core/             (PassportCodec + claims — the only shared runtime lib)
│   └── common-observability/
├── web/                           (Angular shell + micro-frontends)
├── infra/{local,docker,k8s,terraform}/
├── tools/
└── .github/workflows/

Three structural choices worth defending in ADR-0002: contracts/ sits above services/ so no service owns the schema; libs/ stays deliberately thin (shared libs are how microservices quietly become a distributed monolith); infra/local/ holds one docker-compose.yml that boots Postgres + Kafka + Keycloak-as-oracle in a single command.

Naming discipline: NNNN-kebab-title.md, zero-padded, never reused. Never edit an Accepted ADR — write a new one and mark the old Superseded by 00NN.

Sources: MADR is the streamlined Markdown ADR template — adr.github.io/madr, current release 4.0.0, with bare and minimal templates at github.com/adr/madr/tree/4.0.0/template. Broader template comparison at adr.github.io/adr-templates. 
Architectural Decision Records
Architectural Decision Records

Next smallest action: don't accept this yet. Read OAuth 2.1 draft-15 §4.1 (auth code + PKCE) and §7 (security considerations), then come back and challenge exactly two things in the ADR — the 5-minute access TTL and the decision to keep permissions out of the Passport. Agar dono defend ho gaye, flip status to Accepted, commit, and we move to the threat model.