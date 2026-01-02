# 🔐 Ngelmak Vault: Secret management for Ngelmak

This documentation explains how to setup **HashiCorp Vault** as secret managemnt for Ngelmak-Project.  
The goal is to securely manage:

- A **JWT signing key** (for JJWT).
- **Postgres database credentials** (dynamic secrets).
- Transit encryption.
- Etc.

---


## 📦 Prerequisites

Make sure that OpenBao app is running.

Follow the following steps.

---

## 2. Configure the OpenBao for accesses

### KV Secrets Engine

```bash
bao kv put kv/jjwt/dev jwt-secret-key="NzgwODE3NjExMzk1MDFjYzc2NmRjMmM2Yjc0ZTYyMGUxODM3ZThjMzk0ZTliMTE0MjhlNjliOWRhYTI2MzFkN2RkMGU3NDVhYTA0MzRkNTBkNGEzYmZlMzE1MTg4ZjVmYzA5NmFlNTEyZjkyZjYxMGJlMTM1NmU3ZmU0NDg2Yjk="

Success! Data written to: kv/jjwt/dev
```


### Define a Policy

Create `api-gateway-app-policy.hcl`:

```bash
tee /etc/openbao/policies/api-gateway-app-policy.hcl <<EOF
# KV reading JWT secret
path "kv/*" {
  capabilities = ["read"]
}
EOF
```
Load the policy:

```bash
bao policy write ngelmak-api-gateway-policy /etc/openbao/policies/api-gateway-app-policy.hcl

Success! Uploaded policy: ngelmak-api-gateway-policy
```

Create an AppRole:

```bash
bao write auth/approle/role/ngelmak-api-gateway-role \
  policies="ngelmak-api-gateway-policy" \
  secret_id_ttl=48h \
  token_ttl=1h \
  token_max_ttl=4h

Success! Data written to: auth/approle/role/ngelmak-api-gateway-role
```

Fetch Role ID:

```bash
bao read auth/approle/role/ngelmak-api-gateway-role/role-id

Key        Value
---        -----
role_id    e2c81114-da46-d8d2-379d-9145c0c14f30
```

- **auth/approle/role/<role-name>/role-id** → path that returns the Role ID (non‑secret identifier).

Generate Secret ID:

```bash
bao write -f auth/approle/role/ngelmak-api-gateway-role/secret-id

Key                   Value
---                   -----
secret_id             c819d93a-1f84-443d-1152-9ce25619e6fc
secret_id_accessor    9cc91cd8-38ed-ce23-bd07-479e1f6b93b9
secret_id_num_uses    0
secret_id_ttl         48h
```