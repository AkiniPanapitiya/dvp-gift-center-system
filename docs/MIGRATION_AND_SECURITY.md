# Migration and Security Guide

This document clarifies database schema alignment and the finalized security configuration.

## Database schema alignment (no runtime migrations)

- Hibernate DDL is disabled: `spring.jpa.hibernate.ddl-auto=none`.
- The application relies on the provided SQL: `database/dvp_gift_center_schema.sql`.
- Ensure identifiers (PK/FK) are consistently defined (e.g., BIGINT signedness) to avoid foreign key incompatibility errors.
- If you encounter errors like `... referenced column ... are incompatible`, compare your table definitions with the provided schema and align types accordingly.

## Security configuration

- Do not include `/api` in security matchers; it is provided by `server.servlet.context-path=/api`.
- Final rules:
  - Public: `/online/products[/**]`, `/online/categories[/**]`, `/auth/**`, Swagger docs
  - Customer-only: `/online/cart/**`, `/online/checkout[/**]`, `/online/orders/**`
  - Admin-only: `/admin/**`
  - Others: authenticated

Code excerpt:

```java
http
  .cors(c -> c.configurationSource(corsConfigurationSource()))
  .csrf(csrf -> csrf.disable())
  .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
  .authorizeHttpRequests(auth -> auth
    .requestMatchers("/online/products", "/online/products/**").permitAll()
    .requestMatchers("/online/categories", "/online/categories/**").permitAll()
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/online/cart/**").hasRole("CUSTOMER")
    .requestMatchers("/online/checkout", "/online/checkout/**").hasRole("CUSTOMER")
    .requestMatchers("/online/orders/**").hasRole("CUSTOMER")
    .anyRequest().authenticated()
  )
  .authenticationProvider(authenticationProvider())
  .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

## Verification steps

1. Start the backend with the updated configuration.
2. Confirm no foreign key incompatibility errors are emitted on startup.
3. Test public endpoints without a token:
   - `GET /api/online/products`
   - `GET /api/online/categories`
4. Test customer endpoints require auth:
   - `GET /api/online/cart`
5. Test admin endpoints require ADMIN role:
   - `GET /api/admin/...`

## Common pitfalls

- Double-prefixing `/api` in controller mappings and security matchers causes 403s.
- Changing column types (signedness/size) in a live DB without aligning FKs can cause startup errors.
