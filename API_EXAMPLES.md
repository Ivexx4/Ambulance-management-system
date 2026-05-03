# Exemplos Práticos - Ambulance & Device Unificados

## Autenticação
Todas as requisições usam Basic Auth:
```bash
-u admin:admin
```

---

## 1️⃣ CRIAR UMA AMBULÂNCIA

### Requisição
```bash
curl -u admin:admin -X POST http://localhost:8082/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Ambulância Central 01",
    "uniqueId":"AMB-CC-001",
    "phone":"911123456",
    "model":"Toyota Hiace",
    "contact":"João Silva",
    "deviceType":"ambulance",
    "ambulanceStatus":"available",
    "ambulanceNotes":"Veículo em perfeito estado, pronto para operação"
  }'
```

### Resposta (200 OK)
```json
{
  "id": 1,
  "name": "Ambulância Central 01",
  "uniqueId": "AMB-CC-001",
  "phone": "911123456",
  "model": "Toyota Hiace",
  "contact": "João Silva",
  "deviceType": "ambulance",
  "ambulanceStatus": "available",
  "ambulanceNotes": "Veículo em perfeito estado, pronto para operação",
  "driverId": null,
  "status": "offline",
  "disabled": false
}
```

---

## 2️⃣ LISTAR TODAS AS AMBULÂNCIAS

### Requisição (Novo Endpoint - Recomendado)
```bash
curl -u admin:admin http://localhost:8082/api/devices?deviceType=ambulance
```

### Requisição (Legacy - Ainda Funciona)
```bash
curl -u admin:admin http://localhost:8082/api/ambulances
```

### Resposta (200 OK)
```json
[
  {
    "id": 1,
    "name": "Ambulância Central 01",
    "uniqueId": "AMB-CC-001",
    "deviceType": "ambulance",
    "ambulanceStatus": "available",
    "status": "offline"
  },
  {
    "id": 2,
    "name": "Ambulância Central 02",
    "uniqueId": "AMB-CC-002",
    "deviceType": "ambulance",
    "ambulanceStatus": "busy",
    "status": "online"
  }
]
```

---

## 3️⃣ FILTRAR AMBULÂNCIAS POR CRITÉRIO

### Buscar por palavra-chave
```bash
curl -u admin:admin "http://localhost:8082/api/devices?deviceType=ambulance&keyword=Central"
```

### Filtrar com paginação
```bash
curl -u admin:admin "http://localhost:8082/api/devices?deviceType=ambulance&limit=10&offset=0"
```

### Buscar ID específico
```bash
curl -u admin:admin "http://localhost:8082/api/devices?deviceType=ambulance&id=1"
```

---

## 4️⃣ OBTER DETALHES DE UMA AMBULÂNCIA

### Requisição
```bash
curl -u admin:admin http://localhost:8082/api/devices/1
```

### Resposta (200 OK)
```json
{
  "id": 1,
  "name": "Ambulância Central 01",
  "uniqueId": "AMB-CC-001",
  "phone": "911123456",
  "model": "Toyota Hiace",
  "contact": "João Silva",
  "deviceType": "ambulance",
  "ambulanceStatus": "available",
  "ambulanceNotes": "Veículo em perfeito estado, pronto para operação",
  "driverId": null,
  "status": "offline",
  "lastUpdate": "2026-05-03T10:30:00Z",
  "disabled": false,
  "expirationTime": null
}
```

---

## 5️⃣ ATUALIZAR AMBULÂNCIA

### Requisição - Atualizar Status
```bash
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "ambulanceStatus": "busy",
    "ambulanceNotes": "Em operação de emergência. Ligue para 911"
  }'
```

### Requisição - Atribuir Motorista
```bash
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "driverId": 5
  }'
```

### Requisição - Atualizar Vários Campos
```bash
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Ambulância Central 01 - Renovada",
    "phone": "911654321",
    "ambulanceStatus": "available",
    "ambulanceNotes": "Manutenção realizada. Pronto para ação.",
    "driverId": 3
  }'
```

### Resposta (204 No Content ou 200 OK)
```
Status: 204 No Content
```

---

## 6️⃣ ATRIBUIR MOTORISTA/EQUIPE

### Criar Permissão (Driver/Condutor)
```bash
curl -u admin:admin -X POST http://localhost:8082/api/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": 1,
    "driverId": 5
  }'
```

### Listar Permissões de um Dispositivo
```bash
curl -u admin:admin "http://localhost:8082/api/permissions?deviceId=1"
```

### Remover Permissão
```bash
curl -u admin:admin -X DELETE http://localhost:8082/api/permissions/PERMISSION_ID
```

---

## 7️⃣ DELETAR AMBULÂNCIA

### Requisição
```bash
curl -u admin:admin -X DELETE http://localhost:8082/api/devices/1
```

### Resposta (204 No Content)
```
Status: 204 No Content
```

---

## 8️⃣ CASOS DE USO COMUNS

### Cenário 1: Registrar nova ambulância no sistema
```bash
# 1. Criar device como ambulância
AMBULANCE=$(curl -s -u admin:admin -X POST http://localhost:8082/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Ambulância Norte 01",
    "uniqueId":"AMB-N-001",
    "deviceType":"ambulance",
    "ambulanceStatus":"available"
  }')

AMBULANCE_ID=$(echo $AMBULANCE | jq -r '.id')

# 2. Atribuir motorista (ID 3)
curl -u admin:admin -X POST http://localhost:8082/api/permissions \
  -H "Content-Type: application/json" \
  -d "{\"deviceId\": $AMBULANCE_ID, \"driverId\": 3}"

echo "Ambulância criada com ID: $AMBULANCE_ID"
```

### Cenário 2: Despachar ambulância para emergência
```bash
# Marcar ambulância como em operação
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "ambulanceStatus": "busy",
    "ambulanceNotes": "Despachada para Rua Principal, 123. Paciente cardíaco"
  }'
```

### Cenário 3: Finalizar operação e retomar disponibilidade
```bash
# Marcar ambulância como disponível novamente
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "ambulanceStatus": "available",
    "ambulanceNotes": "Retorno à base. Próxima disponível para despacho"
  }'
```

### Cenário 4: Manutenção programada
```bash
# Marcar ambulância como em manutenção
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "ambulanceStatus": "maintenance",
    "ambulanceNotes": "Manutenção preventiva. Retorno previsto: 10/05/2026"
  }'
```

---

## 9️⃣ CAMPOS DISPONÍVEIS

### Campos Passables (ao criar/atualizar)
```json
{
  "name": "Texto (128 chars)",
  "uniqueId": "Identificador único (128 chars)",
  "phone": "Telefone (opcional)",
  "model": "Modelo do veículo (opcional)",
  "contact": "Contacto (opcional)",
  "category": "Categoria (opcional)",
  "deviceType": "vehicle|ambulance|...",
  "ambulanceStatus": "available|busy|maintenance|...",
  "ambulanceNotes": "Notas livres (4000 chars)",
  "driverId": "ID do motorista (opcional)",
  "disabled": "true|false",
  "expirationTime": "Data de expiração (ISO 8601)"
}
```

### Campos Read-Only (retornados)
```json
{
  "id": "ID gerado pelo sistema",
  "status": "online|offline|unknown (GPS status)",
  "lastUpdate": "Data/hora última posição",
  "positionId": "ID última posição",
  "groupId": "ID do grupo",
  "attributes": "Atributos customizados"
}
```

---

## 🔟 STATUS HTTP ESPERADOS

| Operação | Status | Significado |
|----------|--------|-------------|
| POST | 200 | Criado com sucesso |
| GET | 200 | Obtido com sucesso |
| PUT | 204 | Atualizado com sucesso |
| DELETE | 204 | Deletado com sucesso |
| POST/PUT | 400 | Validação falhou |
| GET | 403 | Sem permissão |
| GET | 404 | Não encontrado |
| POST/PUT | 409 | Conflito (ex: uniqueId duplicado) |
| * | 500 | Erro interno servidor |

---

## 📱 Usando no Frontend React

### Criar Ambulância
```javascript
const response = await fetch('/api/devices', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: 'Ambulância 01',
    uniqueId: 'AMB-001',
    deviceType: 'ambulance',
    ambulanceStatus: 'available'
  })
});
const newAmbulance = await response.json();
```

### Listar Ambulâncias
```javascript
const response = await fetch('/api/devices?deviceType=ambulance');
const ambulances = await response.json();
```

### Atualizar Status
```javascript
await fetch(`/api/devices/${id}`, {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    id,
    ambulanceStatus: 'busy',
    ambulanceNotes: 'Em operação'
  })
});
```

---

## 🎯 Migração de Código Antigo

### Antes (Endpoint Antigo)
```bash
curl -u admin:admin http://localhost:8082/api/ambulances
curl -u admin:admin http://localhost:8082/api/ambulances/1
```

### Depois (Novo Endpoint - Recomendado)
```bash
curl -u admin:admin http://localhost:8082/api/devices?deviceType=ambulance
curl -u admin:admin http://localhost:8082/api/devices/1
```

**Ambos funcionam!** Você pode migrar gradualmente.

---

## ✅ Validação Rápida

Teste se tudo está funcionando:

```bash
# 1. Criar
AMB_ID=$(curl -s -u admin:admin -X POST http://localhost:8082/api/devices \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","uniqueId":"TEST-123","deviceType":"ambulance"}' | jq -r '.id')

# 2. Listar
curl -s -u admin:admin http://localhost:8082/api/devices?deviceType=ambulance | jq

# 3. Obter
curl -s -u admin:admin http://localhost:8082/api/devices/$AMB_ID | jq

# 4. Atualizar
curl -u admin:admin -X PUT http://localhost:8082/api/devices/$AMB_ID \
  -H "Content-Type: application/json" \
  -d '{"id":'$AMB_ID',"ambulanceStatus":"busy"}'

# 5. Deletar
curl -u admin:admin -X DELETE http://localhost:8082/api/devices/$AMB_ID

echo "✅ Tudo funcionando corretamente!"
```

---

**Última Atualização**: 3 de Maio de 2026  
**Versão**: 1.0  
**Status**: Production Ready ✅

