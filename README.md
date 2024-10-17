
# e-invoice-visualizer

![](version-badge.svg)

This microservice visualizes CII/UBL e-invoices by converting them into HTML or PDF formats. It provides three main HTTP endpoints to interact with the service.

## HTTP Endpoints

### 1. Health Check

Checks the health status of the service.

**Endpoint:**
```
GET /health
```

**Example:**
```sh
curl -X GET http://localhost:8080/health
```

### 2. Convert XML to HTML

Converts a given XML e-invoice to an HTML file.

**Endpoint:**
```
POST /convert.html
```
**Example response:**
```json
{
  "version": "0.1.10 (ba8cecca)",
  "freeMemory": 254288368,
  "totalMemory": 268435456,
  "uptime": 94859746,
  "uptimeString": "1d 2h 20m 59s"
}
```

**Headers:**
- `Content-Type: application/xml`
- `Accept-Language: <language_code>` (optional, e.g., `de` for German)  
  Valid: `de` or `en`, default: `de`

**Example:**
```sh
curl -X POST http://localhost:8080/convert.html \
     -H "Content-Type: application/xml" \
     -H "Accept-Language: en" \
     --data-binary @path/to/your/e-invoice.xml
```

### 3. Convert XML to PDF

Converts a given XML e-invoice to a PDF file.

**Endpoint:**
```
POST /convert.pdf
```

**Headers:**
- `Content-Type: application/xml`
- `Accept-Language: <language_code>` (optional, e.g., `de` for German)  
  Valid: `de` or `en`, default: `de`

**Example:**
```sh
curl -X POST http://localhost:8080/convert.pdf \
     -H "Content-Type: application/xml" \
     -H "Accept-Language: en" \
     --data-binary @path/to/your/e-invoice.xml
```

## Build a new version

To build a new version follow these steps:

1. Increase the version number in the `gradle.properties` file.
2. Run `./gradlew createVersionBadge` to create updated version badge.
3. In necessary, `make` to update external dependencies.
4. Push changes to the repository.
5. Create, review and merge a pull request.
6. Create a new release in GitHub with the new version number as tag.
