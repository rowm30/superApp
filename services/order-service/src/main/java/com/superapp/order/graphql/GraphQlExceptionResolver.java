package com.superapp.order.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

// Bina iske exception poore stack trace ke saath client ko chali jaati —
// leaks internal details. Isse "FORBIDDEN" type ka clean GraphQL error milta hai.
@Component
public class GraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof AccessDeniedGraphQlException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.FORBIDDEN)
                    .build();
        }
        return null; // baaki sab default handling pe chhod do
    }
}