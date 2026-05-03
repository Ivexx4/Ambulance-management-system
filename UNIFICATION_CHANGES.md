# Unificação de Ambulance e Device - Resumo das Mudanças

## Overview
Unificação completa das classes `Ambulance` e `Device` para eliminar duplicação de código e complexidade arquitetural. Agora, uma ambulância é simplesmente um Device com `deviceType='ambulance'` e campos específicos de ambulância.

## Mudanças no Backend (Java)

### 1. Classe Device.java
**Adicionados campos:**
- `driverId` (Long) - ID do motorista/condutor
- `ambulanceStatus` (String) - Status específico da ambulância (available, busy, etc.)
- `ambulanceNotes` (String) - Notas adicionais da ambulância
- `deviceType` (String) - Tipo do dispositivo (vehicle, ambulance, etc.)

### 2. Classe Ambulance.java
- **REMOVIDA** - Não é mais necessária

### 3. AmbulanceResource.java
- **RECRIADA** como wrapper para compatibilidade com API legada
- `/api/ambulances` agora delegará para `/api/devices?deviceType=ambulance`

### 4. DeviceResource.java
- Adicionado parâmetro `@QueryParam("deviceType")` ao método GET
- Suporta filtrar devices por tipo: `/api/devices?deviceType=ambulance`
- Mantém compatibilidade com requests anteriores

### 5. Liquibase Migration (changelog-merge-ambulance-device.xml)
**Mudanças no banco de dados:**
- Adicionadas colunas a `tc_devices`: `driverid`, `ambulancestatus`, `ambulancenotes`, `devicetype`
- Migrados dados de `tc_ambulances` para `tc_devices`
- Removidas tabelas legacy: `tc_ambulances`, `tc_user_ambulance`
- Adicionada constraint FK para `driverid`

## Mudanças no Frontend (React)

### 1. RegisterAmbulancePage.jsx
- Cria Device com `deviceType: 'ambulance'` em vez de usar endpoint separado
- Define `ambulanceStatus: 'available'` por padrão

### 2. AmbulancesPage.jsx
- Endpoint atualizado: `/api/ambulances` → `/api/devices?deviceType=ambulance`
- Endpoint de actions: `ambulances` → `devices`
- Display de status atualizado para usar `ambulanceStatus`

### 3. AmbulancePage.jsx
- Endpoint atualizado: `ambulances` → `devices`
- Campos mapeados: `status` → `ambulanceStatus`, `notes` → `ambulanceNotes`
- Usa `item.id` em vez de `item.deviceId` para permissões

### 4. DispatchPage.jsx
- Filtra apenas devices com `deviceType === 'ambulance'`
- Exibe `ambulanceStatus` em vez de `status`

## Benefícios da Unificação

1. **Eliminação de Duplicação**: Uma única classe `Device` serve para todos os tipos
2. **Estrutura Simplificada**: Menos tabelas, menos classes, menos endpoints
3. **Maior Flexibilidade**: Fácil adicionar novos tipos de dispositivos (vehicle, truck, etc.)
4. **API Consistente**: Todos os dispositivos através de `/api/devices`
5. **Melhor Manutenção**: Um único modelo para gerenciar

## Compatibilidade Backwards

- Endpoint `/api/ambulances` ainda está disponível (wrapper)
- Mudancia de schema é zero-downtime com Liquibase
- Clientes antigos continuam funcionando durante período de transição

## Próximos Passos Recomendados

1. Executar o build: `./gradlew.bat clean assemble`
2. Testar endpoints:
   - `GET /api/devices?deviceType=ambulance` (novo)
   - `GET /api/ambulances` (legacy)
3. Migrar clientes para usar `/api/devices?deviceType=ambulance`
4. Remover AmbulanceResource após período de transição

## Testes Recomendados

```bash
# Criar ambulância
curl -u admin:admin -X POST http://localhost:8082/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name":"AMB-01",
    "uniqueId":"AMB-001",
    "deviceType":"ambulance",
    "ambulanceStatus":"available",
    "ambulanceNotes":"Pronta para operação"
  }'

# Listar ambulâncias (novo)
curl -u admin:admin http://localhost:8082/api/devices?deviceType=ambulance

# Listar ambulâncias (legacy)
curl -u admin:admin http://localhost:8082/api/ambulances

# Atualizar ambulância
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{"ambulanceStatus":"busy","ambulanceNotes":"Em operação"}'
```

## Arquivos Modificados

### Backend
- `src/main/java/org/traccar/model/Device.java` - Adicionado campos
- `src/main/java/org/traccar/api/resource/DeviceResource.java` - Adicionado filtro deviceType
- `src/main/java/org/traccar/api/resource/AmbulanceResource.java` - Recriado como wrapper
- `schema/changelog-merge-ambulance-device.xml` - Nova migração
- `schema/changelog-master.xml` - Incluído nova migração

### Frontend
- `traccar-web/src/pages/RegisterAmbulancePage.jsx` - Atualizado para usar devices
- `traccar-web/src/settings/AmbulancesPage.jsx` - Atualizado para usar endpoint devices
- `traccar-web/src/settings/AmbulancePage.jsx` - Atualizado para usar endpoint devices
- `traccar-web/src/pages/DispatchPage.jsx` - Atualizado para filtrar ambulâncias

## Arquivos Removidos

- `src/main/java/org/traccar/model/Ambulance.java` - Classe removida

