package com.superapp.order.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
// ⚠️ Table ka naam "orders" hai, "order" nahi —
// kyunki ORDER SQL ka reserved word hai (ORDER BY).
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    // @Enumerated(STRING) = database mein "PLACED" likho.
    // ⚠️ Default ORDINAL hai — wo 0, 1, 2 likhta hai.
    //    Enum mein beech mein ek naya status add kiya, toh saare
    //    purane records ka matlab badal jayega. NEVER use ORDINAL.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    // @OneToMany = "ek order ke bahut saare items"
    // mappedBy = "OrderItem class ke 'order' field ne pehle se ye rishta define kiya hai"
    //            — bina iske JPA ek teesri join table bana dega.
    // cascade = ALL → order save karo, items apne aap save
    // orphanRemoval = list se item hataya toh database se bhi delete
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();


    public Order() {
    }

    public Order(String buyerId) {
        this.buyerId = buyerId;
        this.status = OrderStatus.PLACED;
        this.totalAmount = BigDecimal.ZERO;
    }

    // ⚠️ Ye method zaroori hai. Sirf items.add() karne se
    // OrderItem ka 'order' field null reh jaata hai, aur database
    // order_id NOT NULL par error de deta hai.
    // Dono taraf jodna padta hai — isse "helper method" kehte hain.
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    // Saare items ka total jod do
    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public String getBuyerId() { return buyerId; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }

    public void setStatus(OrderStatus status) { this.status = status; }
}