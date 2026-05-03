# Guia de Testes - Unificação de Ambulance e Device

## Pré-requisitos
- Servidor rodando em `http://localhost:8082`
- Credenciais: `admin:admin`
- Ferramentas: `curl` ou Postman

## Testes de API

### 1. Criar uma Ambulância (como Device)
```bash
curl -u admin:admin -X POST http://localhost:8082/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Ambulância 01",
    "uniqueId":"AMB-001",
    "deviceType":"ambulance",
    "ambulanceStatus":"available",
    "ambulanceNotes":"Veículo pronto para operação"
  }'
```

**Resposta esperada:** Device criado com ID. Anote o ID para os próximos testes.

### 2. Listar Ambulâncias (novo endpoint)
```bash
curl -u admin:admin http://localhost:8082/api/devices?deviceType=ambulance
```

**Esperado:** JSON array com todos os devices do tipo 'ambulance'

### 3. Listar Ambulâncias (endpoint legacy)
```bash
curl -u admin:admin http://localhost:8082/api/ambulances
```

**Esperado:** Mesmo resultado do endpoint novo (compatibilidade backwards)

### 4. Atualizar uma Ambulância
```bash
curl -u admin:admin -X PUT http://localhost:8082/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id":1,
    "name":"Ambulância 01 - Atualizada",
    "ambulanceStatus":"busy",
    "ambulanceNotes":"Em operação de emergência"
  }'
```

**Experado:** Status 204 (No Content) ou 200 (objeto atualizado)

### 5. Obter Detalhes da Ambulância
```bash
curl -u admin:admin http://localhost:8082/api/devices/1
```

**Esperado:** Objeto device com todos os campos incluindo:
- `deviceType: "ambulance"`
- `ambulanceStatus: "busy"`
- `ambulanceNotes: "Em operação de emergência"`
- `driverId: (null ou ID do motorista)`

### 6. Deletar uma Ambulância
```bash
curl -u admin:admin -X DELETE http://localhost:8082/api/devices/1
```

**Esperado:** Status 204 (No Content)

### 7. Filtrar por Multiple Parameters
```bash
curl -u admin:admin "http://localhost:8082/api/devices?deviceType=ambulance&keyword=01&limit=10"
```

**Esperado:** Ambulâncias que correspondem aos critérios

## Testes de Interface (UI)

### 1. Página de Ambulâncias (`/settings/ambulances`)
- [ ] Página carrega sem erros
- [ ] Lista exibe ambulâncias
- [ ] Busca por palavra-chave funciona
- [ ] Status é exibido corretamente
- [ ] Botão de editar funciona
- [ ] Botão de deletar funciona
- [ ] Botão "+" para criar nova funciona

### 2. Formulário de Ambulância (`/settings/ambulance`)
- [ ] Campos estão visíveis: Name, Unique ID, Status, Notes
- [ ] Seção "Crew Assignment" está disponível
- [ ] Pode selecionar driver
- [ ] Pode selecionar paramedicos
- [ ] Botão Save funciona
- [ ] Alterações são perisistidas

### 3. Página de Registro (`/pages/register-ambulance`)
- [ ] Formulário carrega sem erros
- [ ] Campo Name obrigatório
- [ ] Campo GPS Device Unique ID obrigatório
- [ ] Pode selecionar Driver
- [ ] Pode selecionar Paramedics
- [ ] Botão "Register Ambulance" funciona
- [ ] Confirma sucesso e redireciona
- [ ] Ambulância aparece em `/settings/ambulances`

### 4. Página de Despacho (`/pages/dispatch`)
- [ ] Página carrega sem erros
- [ ] Dropdown lista apenas ambulâncias (não outros devices)
- [ ] Status mostrado corretamente para cada ambulância
- [ ] Pode selecionar ambulância
- [ ] Campo location é obrigatório
- [ ] Botão Dispatch funciona

### 5. Teste de Permissões
- [ ] Usuário não-admin ve apenas suas ambulâncias
- [ ] Usuário admin vê todas as ambulâncias
- [ ] Drivers/Paramedics associados corretamente
- [ ] Podem ser alterados

## Testes de Banco de Dados

### Verificar Migração (após o build)
```sql
-- Verificar se as colunas foram adicionadas a tc_devices
DESCRIBE tc_devices;

-- Deve conter: driverid, ambulancestatus, ambulancenotes, devicetype

-- Verificar dados migrados
SELECT COUNT(*) FROM tc_devices WHERE devicetype = 'ambulance';

-- Verificar se tc_ambulances foi deletada
SELECT * FROM tc_ambulances;  -- Deve retornar erro (tabela não existe)

-- Verificar se tc_user_ambulance foi deletada
SELECT * FROM tc_user_ambulance;  -- Deve retornar erro (tabela não existe)
```

## Testes de Compatibilidade

### 1. Clientes Antigos Continuam Funcionando
```bash
# Novo endpoint com filtro
curl -u admin:admin http://localhost:8082/api/devices?deviceType=ambulance

# Endpoint legacy
curl -u admin:admin http://localhost:8082/api/ambulances

# Ambos devem retornar o mesmo resultado
```

### 2. Campos de Ambulância Acessíveis
```bash
curl -u admin:admin http://localhost:8082/api/devices/1 | jq '.ambulanceStatus, .ambulanceNotes, .driverId'
```

## Testes de Performance

### 1. Listar muitas ambulâncias (pagination)
```bash
curl -u admin:admin "http://localhost:8082/api/devices?deviceType=ambulance&limit=100&offset=0"
```

### 2. Busca com filtro
```bash
curl -u admin:admin "http://localhost:8082/api/devices?deviceType=ambulance&keyword=search_term"
```

## Relatório de Testes

Ao executar os testes, documente:
- [✓/✗] Test ID: (ex: API-1 ou UI-1)
- [✓/✗] Status: Pass/Fail/Skipped
- [✓/✗] Observações: (ex: timeout, erro específico, etc.)

### Exemplo
| Test | Status | Notes |
|------|--------|-------|
| API-1: Create Ambulance | ✓ | Device criado com ID 5 |
| API-2: List Ambulances (new) | ✓ | 1 ambulância retornada |
| API-3: List Ambulances (legacy) | ✓ | Mesmo resultado |
| UI-1: Ambulances Page | ✓ | Carregou sem erros |
| DB-1: Migration | ✓ | Colunas adicionadas OK |

## Troubleshooting

### Erro: "/api/ambulances não encontrado"
- Confirme que AmbulanceResource.java está compilado
- Reinicie o servidor
- Verifique os logs: `grep -i ambulance` no console

### Erro: "Campo ambulanceStatus não reconhecido"
- Confirme que o build incluiu a versão atualizada de Device.java
- Limpe cache do browser
- Faça rebuild: `./gradlew clean build`

### Erro: "Tabela tc_ambulances não encontrada"
- Migração Liquibase não foi executada corretamente
- Verifique changelog-merge-ambulance-device.xml está no master
- Consulte databasechangelog: `SELECT * FROM databasechangelog WHERE id LIKE '%merge%'`

### Dados não aparecem após criar ambulância
- Verifique se `deviceType` foi setado como 'ambulance'
- Confirme `ambulanceStatus` tem um valor (não null)
- Faça refresh da página
- Verifique no DevTools se a requisição foi bem-sucedida

## Próximas Etapas

1. Documenta bugs/issues encontrados
2. Fix any failing tests
3. Schedule removal of legacy endpoints
4. Notify API clients about deprecation
5. Update API documentation

