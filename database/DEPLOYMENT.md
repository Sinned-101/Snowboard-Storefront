# Database Deployment — Aiven for MySQL (Free Plan)

How to host the `snowboard_storefront` database in the cloud using **Aiven for MySQL**,
load the schema and seed data, and connect the Java application.

> **Why Aiven free plan?** No credit card required, always free (not a trial), and it runs
> **standard InnoDB MySQL** — so our foreign keys, `CASCADE`/`RESTRICT` rules, ENUMs, and
> `CHECK` constraints all behave exactly as designed in [`schema.sql`](schema.sql).

> ⚠️ **Security first:** Never commit real credentials or the CA certificate to the repo.
> All host names, passwords, and `ca.pem` files below are **placeholders** — keep the real
> values in environment variables or a git-ignored file. See [Security](#security--secrets).

---

## Free Plan Limits

| Resource | Free plan |
|----------|-----------|
| Credit card | **Not required** |
| Storage | **1 GB** (plenty — our seed data is only a few KB) |
| RAM | 1 GB |
| CPU | 1 vCPU |
| Nodes | 1 (no replicas) |
| Backups | Daily |
| SSL/TLS | **Required** (CA cert provided) |
| Limit | **One free MySQL service per organization** |

> **Team note:** Because there's only **one free MySQL service per organization**, the team
> should share a **single** Aiven service rather than each person creating their own. The
> service owner can invite teammates or share credentials securely.

---

## Step 1 — Create the Service

1. Go to **<https://aiven.io/free-tier>** and sign up (Google or GitHub login works; no
   payment info needed).
2. In the **Aiven Console**, open your project → **Services** → **Create service**.
3. Select **MySQL**.
4. Choose the **Free plan**. (On the free tier you **can't** pick the cloud provider or
   region — it's assigned automatically.)
5. Name it something like `snowboard-storefront-db`, then click **Create service**.
6. Wait for the status to change to **Running** (usually a couple of minutes).

## Step 2 — Get the Connection Details

Open the service → **Overview** / **Quick Connect** tab. Record these (treat as secrets):

| Field | Default / Notes |
|-------|-----------------|
| **Host** | e.g. `mysql-xxxx.aivencloud.com` |
| **Port** | A non-standard port (often `1xxxx`) — **not** 3306 |
| **Database name** | Default: `defaultdb` |
| **User** | Default: **`avnadmin`** |
| **Password** | Auto-generated |
| **SSL mode** | `REQUIRED` |
| **CA certificate** | Download `ca.pem` — needed for secure connections |

## Step 3 — Connect & Verify

Download `ca.pem` first, then from a terminal:

```bash
mysql --host=YOUR_HOST --port=YOUR_PORT \
      --user=avnadmin --password=YOUR_PASSWORD \
      --ssl-mode=REQUIRED --ssl-ca=ca.pem \
      defaultdb
```

If you get a `mysql>` prompt, you're connected.

## Step 4 — Load the Schema & Seed Data

Our scripts target a database named `snowboard_storefront`. Pick **one** approach:

### Option A — Dedicated database (recommended; scripts unchanged)

`schema.sql` creates and uses `snowboard_storefront`. The `avnadmin` user can normally
create databases on Aiven, so this should work as-is:

```bash
# 1) Schema (creates + uses snowboard_storefront, builds all 10 tables)
mysql --host=YOUR_HOST --port=YOUR_PORT --user=avnadmin \
      --password=YOUR_PASSWORD --ssl-mode=REQUIRED --ssl-ca=ca.pem \
      < database/schema.sql

# 2) Seed data
mysql --host=YOUR_HOST --port=YOUR_PORT --user=avnadmin \
      --password=YOUR_PASSWORD --ssl-mode=REQUIRED --ssl-ca=ca.pem \
      < database/seed.sql
```

### Option B — Use the existing `defaultdb`

If `CREATE DATABASE` is ever blocked, comment out the `CREATE DATABASE` / `USE` lines at the
top of `schema.sql` and load the tables straight into `defaultdb` instead.

### Verify the load

```bash
mysql --host=YOUR_HOST --port=YOUR_PORT --user=avnadmin \
      --password=YOUR_PASSWORD --ssl-mode=REQUIRED --ssl-ca=ca.pem \
      snowboard_storefront -e "SHOW TABLES; SELECT COUNT(*) AS users FROM users;"
```

You should see the 10 tables and a user count of 6 (from the seed data — see
[`SEED_DATA.md`](SEED_DATA.md)).

## Step 5 — Connect the Java Application (JDBC)

The Logic tier connects via JDBC. **SSL is mandatory**, so include it in the URL. Add the
**MySQL Connector/J** driver to the project (Maven/Gradle dependency or a bundled `.jar`).

```java
// Build the JDBC connection (SSL required on Aiven)
String host     = System.getenv("DB_HOST");      // e.g. mysql-xxxx.aivencloud.com
String port     = System.getenv("DB_PORT");      // e.g. 12345
String database = System.getenv("DB_NAME");      // snowboard_storefront
String user     = System.getenv("DB_USER");      // avnadmin
String password = System.getenv("DB_PASSWORD");  // from Aiven console — never hard-code

String url = "jdbc:mysql://" + host + ":" + port + "/" + database
           + "?sslMode=REQUIRED"
           + "&serverTimezone=UTC";

Connection conn = DriverManager.getConnection(url, user, password);
```

> If you use the strictest SSL verification (`sslMode=VERIFY_CA`), you'll also need to import
> `ca.pem` into a Java truststore. For coursework, `sslMode=REQUIRED` is usually sufficient.

---

## Security & Secrets

**Never commit credentials or the CA certificate.** Keep real values out of Git:

1. **Use environment variables** (preferred) or a git-ignored file such as `.env` /
   `config.local.properties`. Example `.env` (do **not** commit this):

   ```bash
   DB_HOST=mysql-xxxx.aivencloud.com
   DB_PORT=12345
   DB_NAME=snowboard_storefront
   DB_USER=avnadmin
   DB_PASSWORD=replace-with-real-password
   ```

2. **Keep `ca.pem` out of the repo** — store it locally on each machine, not in Git.

3. **`.gitignore` coverage** — the repo ignores `.env`, `*.env`, `config.local.properties`,
   and certificate files (`*.pem`, `*.crt`).

4. **Seed passwords stay placeholders** — as noted in [`SEED_DATA.md`](SEED_DATA.md), the
   `password_hash` values are development placeholders; real hashing happens in the Java
   layer at registration.

---

## Troubleshooting

| Symptom | Likely cause / fix |
|---------|--------------------|
| `SSL connection error` / connection refused | SSL is required — add `--ssl-mode=REQUIRED` (CLI) or `sslMode=REQUIRED` (JDBC). |
| `Access denied for user` | Wrong user/password, or you forgot `avnadmin`. Re-copy from the Aiven console. |
| `Unknown database 'snowboard_storefront'` | Run `schema.sql` first (Option A), or use `defaultdb` (Option B). |
| `CREATE command denied` / can't create DB | Use **Option B** and load into `defaultdb`. |
| Public Key Retrieval / timezone errors (JDBC) | Add `&allowPublicKeyRetrieval=true` and ensure `serverTimezone=UTC` is set. |
| Service won't start / stuck | Free-tier services can be reclaimed if idle; recreate from the console if needed. |

---

## Related Docs

- [`DATABASE.md`](DATABASE.md) — full schema design and rationale
- [`SEED_DATA.md`](SEED_DATA.md) — sample data and test accounts
- [`ERD_WALKTHROUGH.md`](ERD_WALKTHROUGH.md) — presentation guide for the ERD diagrams
- [`schema.sql`](schema.sql) · [`seed.sql`](seed.sql) — the SQL scripts
