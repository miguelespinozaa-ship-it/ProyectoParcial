import os
import csv
import boto3
import psycopg2
from datetime import datetime

DB_HOST = os.getenv("DB_HOST", "172.31.94.208")
DB_PORT = int(os.getenv("DB_PORT", "8004"))
DB_NAME = os.getenv("DB_NAME", "bd_api_orders")
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "utec")
S3_BUCKET = os.getenv("S3_BUCKET", "cloudeats-lake-luciano")
S3_PREFIX = os.getenv("S3_PREFIX", "orders")

TABLES = ["orders", "order_items"]


def dump_table(conn, table, s3):
    with conn.cursor() as cur:
        cur.execute(f"SELECT * FROM {table}")
        columns = [desc[0] for desc in cur.description]
        rows = cur.fetchall()
    rows = [tuple(str(v).replace(",", ";") if isinstance(v, str) else v for v in row) for row in rows]

    filename = f"/tmp/{table}.csv"
    with open(filename, "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(columns)
        writer.writerows(rows)

    key = f"{S3_PREFIX}/{table}/{table}.csv"
    s3.upload_file(filename, S3_BUCKET, key)
    print(f"[{datetime.now()}] {table}: {len(rows)} filas -> s3://{S3_BUCKET}/{key}")


def main():
    print(f"Conectando a Postgres {DB_HOST}:{DB_PORT}/{DB_NAME}")
    conn = psycopg2.connect(
        host=DB_HOST, port=DB_PORT, dbname=DB_NAME,
        user=DB_USER, password=DB_PASSWORD
    )
    s3 = boto3.client("s3")

    for table in TABLES:
        dump_table(conn, table, s3)

    conn.close()
    print("Ingesta completada")


if __name__ == "__main__":
    main()
