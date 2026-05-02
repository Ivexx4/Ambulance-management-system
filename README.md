# Ambulance Management System (based on Traccar)

## Overview

Ambulance Management System is an open source fleet management and GPS tracking backend tailored for ambulance operations. It extends the Traccar server to support ambulance-centric workflows such as ambulance entities, assignments, and operational statuses. It supports 200+ GPS protocols and 2000+ device models, works with major SQL databases, and exposes an easy-to-use REST API.

This project is a derivative work of the Traccar GPS tracking server (Apache 2.0). See the Third-Party Notices section below for details and attributions.

Related components from the original Traccar ecosystem include:

- Traccar Web App: https://github.com/traccar/traccar-web
- Traccar Manager App: https://github.com/traccar/traccar-manager
- Traccar Client App (mobile tracking): https://github.com/traccar/traccar-client

## Ambulance-specific features

- Ambulance registry (name, uniqueId, status, notes)
- Link ambulances to devices and drivers
- Search, pagination, and permissions compatible with Traccar
- Ready for geofencing, reports, and notifications

## General features (inherited from Traccar)

- Real-time GPS tracking
- Driver behaviour monitoring
- Detailed and summary reports
- Geofencing functionality
- Alarms and notifications
- Account and device management
- Email and SMS support

## Build

Follow Traccar's official guide to build from source: https://www.traccar.org/build/

Project name (Gradle): ambulance-management-system

## Third-Party Notices and Attributions

This project includes or derives from the following third-party components:

- Traccar (server): Copyright © Traccar. Licensed under the Apache License, Version 2.0. https://www.traccar.org/
- Traccar Web: Copyright © Traccar. Licensed under the Apache License, Version 2.0. https://github.com/traccar/traccar-web
- Jakarta EE APIs (jakarta.*): Licensed under the Eclipse Public License or other compatible licenses. https://jakarta.ee/
- Liquibase: Licensed under the Apache License, Version 2.0. https://www.liquibase.org/
- FasterXML Jackson: Licensed under the Apache License, Version 2.0. https://github.com/FasterXML/jackson

For full license texts, see LICENSE.txt and NOTICE.txt.

## License

Eclipse Public License, Version 2.0

Licensed under the Apache License, Version 2.0 (the "License");
You may not use this file except in compliance with the License.
You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Configuração e Testes (Setup)

Estas são instruções rápidas, em PT, para preparar o ambiente e testar a API localmente.

Pré‑requisitos
- Java JDK 17+ (verifique com: java -version)
- Git (opcional, para clonar)
- Não precisa instalar Gradle: o wrapper (gradlew/gradlew.bat) já está incluído
- Opcional: Postman/Insomnia ou curl para chamadas à API

1) Obter o código
- Clonar: git clone https://seu-repo/ambulance-management-system.git
- Ou copie o projeto para uma pasta local

2) Compilar
- No Windows (PowerShell ou CMD, na pasta do projeto):
  .\gradlew.bat assemble
- No Linux/macOS (Terminal, na pasta do projeto):
  ./gradlew assemble

O build gera:
- target/ambulance-management-system.jar (aplicação)
- target/lib/ (todas as dependências)

3) Base de Dados para testes
- Por omissão, o projeto já inclui H2 (embutido) e será criado em ficheiro/ memória conforme a configuração.
- Para começar rapidamente, use o ficheiro de configuração debug.xml que já está no raiz do projeto.

4) Arrancar o servidor
- Windows (PowerShell/CMD):
  java -cp "target/*;target/lib/*" org.traccar.Main debug.xml
- Linux/macOS (bash/zsh):
  java -cp "target/*:target/lib/*" org.traccar.Main debug.xml

Notas:
- Porta por omissão: 8082 (UI web em http://localhost:8082, API em http://localhost:8082/api)
- Credenciais padrão: admin / admin (mude a palavra-passe no primeiro login)

5) Verificar o estado (Health Check)
- Browser: http://localhost:8082/api/health (deve devolver "OK")
- curl:
  Windows (PowerShell):
    curl http://localhost:8082/api/health
  Linux/macOS:
    curl -s http://localhost:8082/api/health

6) Autenticação
Pode usar Basic Auth (mais simples) ou criar sessão.
- Exemplo Basic Auth com curl (user: admin, pass: admin):
  Windows (PowerShell):
    curl -u admin:admin http://localhost:8082/api/devices
  Linux/macOS:
    curl -u admin:admin http://localhost:8082/api/devices

7) Testar recursos de Ambulâncias e Condutores
- Criar um Condutor (Driver):
  Windows (PowerShell):
    curl -u admin:admin -H "Content-Type: application/json" -d '{"name":"João Silva","uniqueId":"DRV-001"}' http://localhost:8082/api/drivers
  Linux/macOS:
    curl -u admin:admin -H 'Content-Type: application/json' -d '{"name":"João Silva","uniqueId":"DRV-001"}' http://localhost:8082/api/drivers

- Criar uma Ambulância:
  Windows (PowerShell):
    curl -u admin:admin -H "Content-Type: application/json" -d '{"name":"AMB 01","uniqueId":"AMB-001","status":"available","notes":"Pronta"}' http://localhost:8082/api/ambulances
  Linux/macOS:
    curl -u admin:admin -H 'Content-Type: application/json' -d '{"name":"AMB 01","uniqueId":"AMB-001","status":"available","notes":"Pronta"}' http://localhost:8082/api/ambulances

- Listar Ambulâncias:
  curl -u admin:admin http://localhost:8082/api/ambulances

- Atualizar Ambulância (ex.: id 1):
  curl -u admin:admin -X PUT -H "Content-Type: application/json" -d '{"id":1,"name":"AMB 01","uniqueId":"AMB-001","status":"busy"}' http://localhost:8082/api/ambulances/1

- Remover Ambulância (ex.: id 1):
  curl -u admin:admin -X DELETE http://localhost:8082/api/ambulances/1

8) Parar o servidor
- Prima Ctrl+C no terminal onde o servidor está a correr.

9) Limpar (opcional)
- Apagar artefactos de build:
  Windows:  rmdir /s /q target
  Linux/macOS:  rm -rf target

10) Problemas comuns
- Porta 8082 ocupada: mude a porta em debug.xml (procure <entry key="web.port">) ou feche o processo que usa a porta.
- "java not found": instale o JDK 17 e garanta que o PATH aponta para a pasta bin do Java.
- Falhas de autenticação: confirme credenciais admin/admin (primeiro acesso) e evite caracteres especiais escapando corretamente no terminal.

Dica: pode também usar um cliente gráfico (Postman/Insomnia). A especificação OpenAPI (openapi.yaml) está no raiz do projeto; importe-a no cliente para ter as rotas e modelos prontos.

## Como correr na IDE (IntelliJ, VS Code, Eclipse, NetBeans)

Estas instruções ajudam a executar o servidor diretamente a partir da sua IDE preferida para facilitar o desenvolvimento e debugging.

Pré-requisitos comuns
- JDK 17 instalado e configurado na IDE
- Projeto aberto como projeto Gradle
- Ficheiro debug.xml no diretório raiz do projeto (já incluído)
- Porta 8082 livre (ou altere a porta no debug.xml)

IntelliJ IDEA (Community ou Ultimate)
- File > Open... e selecione a pasta do projeto
- Verifique o JDK: File > Project Structure > Project SDK = 17
- Faça um build inicial: View > Tool Windows > Gradle > Tasks > build > assemble
- Crie uma Run Configuration:
  - Run > Edit Configurations... > + > Application
  - Name: Ambulance Server (debug)
  - Main class: org.traccar.Main
  - Program arguments: debug.xml
  - Working directory: a raiz do projeto (onde está o debug.xml)
  - Use classpath of module: selecione o módulo principal do projeto
- Clique em Run ou Debug
- Teste: http://localhost:8082/api/health deve devolver "OK"

VS Code
- Extensões recomendadas: Extension Pack for Java (Language Support, Debugger, Test Runner, etc.) e Gradle for Java
- File > Open Folder... e escolha a pasta do projeto
- Certifique-se que o JDK 17 está selecionado (Java: Configure Java Runtime)
- Build inicial: Terminal > Run Task... > Gradle > assemble (ou use a vista Gradle)
- Adicione um ficheiro .vscode/launch.json com a seguinte configuração:

```
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Run Ambulance Server (debug.xml)",
      "request": "launch",
      "mainClass": "org.traccar.Main",
      "cwd": "${workspaceFolder}",
      "args": ["debug.xml"],
      "vmArgs": "-Dfile.encoding=UTF-8"
    }
  ]
}
```

- Inicie pelo menu Run and Debug escolhendo a configuração acima

Eclipse (com Buildship Gradle)
- File > Import... > Gradle > Existing Gradle Project e selecione a pasta do projeto
- Project > Properties > Java Compiler > Compiler compliance level = 17
- Project > Properties > Java Build Path > Libraries: confirme o JRE 17
- Run > Run Configurations... > Java Application
  - Name: Ambulance Server (debug)
  - Project: selecione este projeto
  - Main class: org.traccar.Main
  - Arguments > Program arguments: debug.xml
  - Arguments > Working directory: pasta raiz do projeto
- Run/Debug

NetBeans (com suporte Gradle)
- File > Open Project... e selecione a pasta do projeto
- Tools > Java Platforms > adicione/seleciona JDK 17
- Clique direito no projeto > Properties > Run
  - Main Class: org.traccar.Main
  - Arguments: debug.xml
  - Working Directory: raiz do projeto
- Run/Debug o projeto

Dicas de Debug
- Breakpoints: pode colocar breakpoints em handlers REST (por ex., org.traccar.api.resource.*)
- Hot reload: recompilar com a IDE/Gradle e reiniciar rapidamente em Debug
- Logs: a saída no console da IDE mostrará os logs do servidor

Resolução de Problemas na IDE
- Class not found/Main class não arranca: faça um build Gradle (assemble) e verifique o módulo na configuração de execução
- Porta 8082 ocupada: altere <entry key="web.port"> no debug.xml ou feche o processo que usa a porta
- Ficheiro debug.xml não encontrado: verifique o Working Directory e o Program arguments
- JDK incorreto: mude para JDK 17 nas definições da IDE
- Acesso à API: use http://localhost:8082/api e autenticação admin/admin (primeira vez)
