# MCP Product Server with AI Agent
[![M8ven Score](https://m8ven.ai/badge/mcp/aamish8-mcp-product-server-with-ai-agent-1pqlsp?v=3ea7ff5b7aace084d68d271778ec419a)](https://m8ven.ai/mcp/aamish8-mcp-product-server-with-ai-agent-1pqlsp?s=readme)

An MCP server integrated with an AI agent...
A learning project with two Spring Boot apps that work together through the **Model Context Protocol (MCP)**:

- **`my_first_mcp`**: an MCP server that exposes product database operations as AI-callable tools.
- **`my-agent`**: an AI agent (Spring AI `ChatClient` + Ollama) that connects to the MCP server and uses its tools.

The server also works with **Claude Desktop**, so the same tools can be used from a chat window or from the custom agent.

## Architecture

```
Claude Desktop ──► mcp-remote ──┐
                                ├──► MCP Server (:8081 /mcp) ──► PostgreSQL
Agent (:8082) ──► MCP Client ───┘
   │
   └──► Ollama (local LLM)
```

The LLM decides *which* tool to call and with *which* arguments. The Java code in the MCP server does the actual work.

## Tech stack

- Java 21+, Spring Boot 4.1.x
- Spring AI 2.0.x (MCP server, MCP client, Ollama)
- Spring Data JPA, PostgreSQL
- Lombok
- Ollama (local LLM) or Claude Desktop

## Tools exposed by the MCP server

| Tool | Description |
|---|---|
| `listProducts` | List all products |
| `addProduct` | Add a product with a name and price |
| `findProductByName` | Find products by name (partial, case-insensitive) |
| `updatePrice` | Update a product's price by id |
| `deleteProduct` | Delete a product by id |

Inputs are validated in Java (for example, prices must be greater than 0 and ids must exist), so invalid requests return clear errors no matter which LLM is calling.

## Prerequisites

- JDK 21 or higher
- PostgreSQL running locally with a database named `mcpdb`
- Node.js (only needed for Claude Desktop via `mcp-remote`, or for MCP Inspector)
- Ollama with a model pulled (only needed for the agent)

## Running the project

### 1. Create the database

```sql
CREATE DATABASE mcpdb;
```

### 2. Set the database password

The password is read from an environment variable (PowerShell shown):

```powershell
$env:DB_PASSWORD="your_password"
```

### 3. Start the MCP server (port 8081)

```bash
cd my_first_mcp
./mvnw spring-boot:run
```

The MCP endpoint is available at `http://localhost:8081/mcp`.

### 4. Test with MCP Inspector (optional)

```bash
npx @modelcontextprotocol/inspector
```

Add a **Streamable HTTP** server with the URL `http://localhost:8081/mcp`, then list and call the tools.

### 5. Use it with Claude Desktop

Add this to `claude_desktop_config.json`, then fully restart Claude Desktop:

```json
{
  "mcpServers": {
    "product-server": {
      "command": "npx",
      "args": ["-y", "mcp-remote", "http://localhost:8081/mcp"]
    }
  }
}
```

Then ask things like *"Add a product called Keyboard for 49.99"* or *"List all products"*.

### 6. Run the custom agent (port 8082)

Make sure the MCP server and Ollama are running, then:

```bash
cd my-agent
./mvnw spring-boot:run
```

Try it in the browser:

```
http://localhost:8082/chat?message=list all products
```

## Key configuration

**MCP server** (`my_first_mcp/src/main/resources/application.properties`)

```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/mcpdb
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.ai.mcp.server.protocol=streamable
```

**Agent** (`my-agent/src/main/resources/application.properties`)

```properties
server.port=8082
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2
spring.ai.mcp.client.streamable-http.connections.product-server.url=http://localhost:8081
```

## Things I learned

- MCP is a connector standard: the server exposes tools, and any MCP client can use them.
- The server needs no LLM. The client side (Claude Desktop or the agent) provides the model.
- Tool-calling quality depends heavily on the model. Claude handled the tools reliably, while a small local model (Llama 3.2, 3B) sometimes picked wrong arguments or reported success after an error.
- Hard rules belong in the Java code, not just in tool descriptions, because descriptions are only suggestions to the LLM.

## Notes

- This is a learning project and is not hardened for production (no authentication on the MCP endpoint).
- Only connect MCP servers you trust, since tools can change real data.
