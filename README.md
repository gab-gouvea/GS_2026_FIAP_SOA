# SwarmBuild

API REST de orquestracao de enxame robotico para ambientes hostis.
Projeto da disciplina de Java (Global Solution - FIAP).

---

## Motivacao

O programa Artemis vai construir uma base lunar **antes** dos astronautas chegarem.
Quem vai construir? Robos. O ambiente lunar tem **poeira altamente abrasiva**, **terreno irregular**
e qualquer falha em um robo na cratera pode comprometer toda a missao - mas a Terra esta a mais
de 380 mil km, com delay de comunicacao de ate 3 segundos. Nao da pra esperar um humano
em Houston decidir o que fazer quando um robo trava.

O **SwarmBuild** e a camada de software que controla o enxame: cada robo manda um
**heartbeat** periodico com bateria, posicao e status. Se um robo para de responder,
o sistema **detecta automaticamente** a falha e **realoca a tarefa dele para outro robo
compativel** que esteja disponivel - sem intervencao humana.

### Aplicacao na Terra (spin-off)

A mesma logica de enxame serve para:

- **Mineracao em zonas de alto risco** - robos escavando onde seria mortal mandar humanos
- **Resgate em desastres** - varredura coordenada de escombros apos terremotos
- **Construcao em zonas radioativas** - decomissionamento de reatores

Em todos esses cenarios, a regra e a mesma: **falhas individuais nao podem parar a missao**.

---

## Como o sistema integra com o problema

```
+---------------+   heartbeat   +-------------+   realoca   +---------------+
|  Robo na Lua  | ------------> | SwarmBuild  | ----------> |  Outro Robo   |
|  (escavadeira)|   bateria,    |   (API)     |   tarefa    |  (escavadeira)|
+---------------+   posicao,    +-------------+             +---------------+
                    status              |
                                        v
                                +---------------+
                                |   Alertas     |
                                |   (BD)        |
                                +---------------+
```

1. Cada robo do enxame envia heartbeat para `POST /api/robos/{id}/heartbeats`
2. O `MonitorDeFalhasService` roda a cada 10 segundos verificando quem nao respondeu
3. Se `ultimoHeartbeat < agora - 60s`, robo e marcado como `FALHA`
4. As tarefas que ele estava executando sao **automaticamente realocadas**
   para outro robo do mesmo tipo via `OrquestradorEnxame`
5. Todo evento critico gera um `Alerta` armazenado no banco

---

## Diagrama de fluxo - realocacao automatica

```mermaid
sequenceDiagram
    participant R1 as Robo A (escavadeira)
    participant API as SwarmBuild API
    participant DB as PostgreSQL
    participant MON as MonitorDeFalhasService
    participant R2 as Robo B (escavadeira)

    R1->>API: POST /heartbeats (bateria 85%, status EM_TAREFA)
    API->>DB: salva heartbeat e atualiza Robo A

    Note over R1: falha hardware - para de transmitir

    loop a cada 10 segundos
        MON->>DB: busca robos com ultimoHeartbeat < agora - 60s
        DB-->>MON: [Robo A]
        MON->>DB: Robo A.status = FALHA, cria Alerta CRITICO
        MON->>DB: busca tarefas em execucao do Robo A
        DB-->>MON: [Tarefa T-001]
        MON->>MON: orquestrador.realocarTarefa(T-001)
        MON->>DB: busca escavadeiras DISPONIVEL
        DB-->>MON: [Robo B]
        MON->>DB: T-001.roboAtribuido = Robo B, status = REALOCADA
        MON->>DB: cria Alerta TAREFA_REALOCADA
    end

    R2->>API: POST /heartbeats (continua execucao de T-001)
```

---

## Stack

- **Java 21**
- **Spring Boot 3.5.7** (web, data-jpa, validation)
- **PostgreSQL 18**
- **Lombok**
- **Springdoc OpenAPI** (Swagger UI)
- **Maven**

---

## Como rodar

### Pre-requisitos

- Java 21+
- PostgreSQL rodando em `localhost:5432`
- Database `swarmbuild` criado:

```bash
psql -d postgres -c "CREATE DATABASE swarmbuild;"
```

### Subir a aplicacao

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.
Swagger UI em `http://localhost:8080/swagger-ui.html`.

### Configuracao do banco

Edite `src/main/resources/application.properties` se seu usuario do Postgres
for diferente de `gabrielgouvea`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/swarmbuild
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

---

## Endpoints principais

### Robos

| Metodo | Caminho                   | Descricao                              |
|--------|---------------------------|----------------------------------------|
| POST   | `/api/robos`              | Cria robo (escavadeira/transportador/montador) |
| GET    | `/api/robos`              | Lista todos                            |
| GET    | `/api/robos/{id}`         | Detalhe                                |
| PATCH  | `/api/robos/{id}/status`  | Atualiza status manualmente            |
| DELETE | `/api/robos/{id}`         | Remove (bloqueado se EM_TAREFA)        |

### Tarefas

| Metodo | Caminho                              | Descricao                         |
|--------|--------------------------------------|-----------------------------------|
| POST   | `/api/tarefas`                       | Cria tarefa                       |
| GET    | `/api/tarefas`                       | Lista                             |
| GET    | `/api/tarefas/{id}`                  | Detalhe                           |
| POST   | `/api/tarefas/{id}/atribuir`         | Atribui automaticamente ao melhor robo |
| POST   | `/api/tarefas/{id}/atribuir/{roboId}` | Atribui a um robo especifico     |
| POST   | `/api/tarefas/{id}/concluir`         | Marca como concluida              |
| POST   | `/api/tarefas/{id}/realocar`         | Forca realocacao para outro robo  |
| DELETE | `/api/tarefas/{id}`                  | Remove                            |

### Heartbeats

| Metodo | Caminho                                  | Descricao                |
|--------|------------------------------------------|--------------------------|
| POST   | `/api/robos/{roboId}/heartbeats`         | Registra heartbeat       |
| GET    | `/api/robos/{roboId}/heartbeats`         | Historico do robo        |

### Alertas

| Metodo | Caminho                       | Descricao                          |
|--------|-------------------------------|------------------------------------|
| GET    | `/api/alertas`                | Lista (filtro `?resolvido=false`)  |
| GET    | `/api/alertas/{id}`           | Detalhe                            |
| POST   | `/api/alertas/{id}/resolver`  | Marca como resolvido               |

---

## Exemplo de uso (curl)

```bash
# 1. Cria duas escavadeiras
curl -X POST http://localhost:8080/api/robos -H "Content-Type: application/json" -d '{
  "codigo": "ESC-001", "nome": "Escavadeira Alpha", "modelo": "LunarDigger v2",
  "tipo": "ESCAVADEIRA", "latitude": -3.1, "longitude": 23.4,
  "capacidadeCargaKg": 500, "profundidadeMaximaMetros": 3.5
}'

curl -X POST http://localhost:8080/api/robos -H "Content-Type: application/json" -d '{
  "codigo": "ESC-002", "nome": "Escavadeira Beta", "modelo": "LunarDigger v2",
  "tipo": "ESCAVADEIRA", "latitude": -3.0, "longitude": 23.5,
  "capacidadeCargaKg": 500, "profundidadeMaximaMetros": 3.5
}'

# 2. Cria uma tarefa de escavacao
curl -X POST http://localhost:8080/api/tarefas -H "Content-Type: application/json" -d '{
  "codigo": "T-001", "descricao": "Escavar cratera setor 5",
  "tipoRoboRequerido": "ESCAVADEIRA", "prioridade": "ALTA",
  "latitude": -3.0, "longitude": 23.4
}'

# 3. Atribui automaticamente ao melhor robo disponivel
curl -X POST http://localhost:8080/api/tarefas/1/atribuir

# 4. Robo A manda heartbeat (vivo)
curl -X POST http://localhost:8080/api/robos/1/heartbeats -H "Content-Type: application/json" -d '{
  "bateria": 85, "latitude": -3.05, "longitude": 23.42,
  "statusReportado": "EM_TAREFA", "mensagem": "tudo certo"
}'

# 5. Para de mandar heartbeat e espera ~70 segundos.
#    O MonitorDeFalhasService vai detectar e realocar para o Robo B.

# 6. Confere os alertas gerados
curl http://localhost:8080/api/alertas
```

---

## Mapeamento dos requisitos do professor

| Requisito                                        | Onde esta no codigo                                                                                  |
|--------------------------------------------------|------------------------------------------------------------------------------------------------------|
| Classes publicas, privadas, estaticas            | `Robo.aoCriar()` (protected), construtores publicos, factory `RoboResponseDTO.de(...)` (static)      |
| Heranca                                          | `Robo` (abstract) -> `RoboEscavadeira`, `RoboTransportador`, `RoboMontador`                          |
| Polimorfismo                                     | `Robo.getTipo()` e `descricaoCapacidade()` sao abstract e cada subclasse implementa diferente        |
| Classes abstratas                                | `Robo` e `abstract`                                                                                  |
| Interfaces + injecao de dependencia              | `OrquestradorEnxame` (interface) injetado em `RoboService`, `TarefaService`, `MonitorDeFalhasService`|
| Modularizacao em metodos                         | Servicos divididos por responsabilidade, metodos privados auxiliares no orquestrador e monitor       |
| Manipulacao de DateTime                          | `LocalDateTime` em heartbeat, tarefas, alertas; `MonitorDeFalhasService.detectarRobosOffline()`      |
| Tratamento de excecoes                           | `exception/` com excecoes customizadas + `GlobalExceptionHandler` (@RestControllerAdvice)            |
| VO / DTO                                         | `Coordenada` (record + @Embeddable - VO) e records em `dto/`                                         |
| Banco de dados                                   | PostgreSQL + JPA com `@Entity`, `@Inheritance`, `@Embedded`, `@ManyToOne`                            |
| WebService / API                                 | Controllers REST em `controller/` com endpoints CRUD + Swagger UI                                    |
| Organizacao                                      | Pacotes `controller`, `service`, `repository`, `model`, `dto`, `exception`                           |

---

## Regras de negocio implementadas

1. **Heartbeat** - cada robo envia status periodicamente; servico atualiza posicao, bateria
   e `ultimoHeartbeat`. Se bateria < 20% gera alerta `BATERIA_BAIXA`.
2. **Deteccao de falha** - job `@Scheduled` roda a cada 10s e marca como `FALHA`
   quem nao mandou heartbeat ha mais de 60s.
3. **Realocacao automatica** - tarefas em execucao do robo falho sao realocadas
   para outro robo do mesmo tipo via `OrquestradorEnxame.realocarTarefa()`.
   Se nao tiver nenhum disponivel, gera alerta `TAREFA_SEM_ROBO_DISPONIVEL`.
4. **Atribuicao inteligente** - ao atribuir tarefa, escolhe o robo do tipo correto
   mais proximo do local da tarefa (distancia euclidiana de coordenadas).
5. **Codigo unico** - robo e tarefa tem `codigo` unico (lanca `CodigoDuplicadoException` 409).
6. **Bloqueio de remocao** - nao deixa deletar robo em tarefa nem tarefa em execucao.
7. **Recuperacao automatica** - se robo em `FALHA` voltar a mandar heartbeat
   reportando outro status, e movido para `DISPONIVEL`.

---

## Autor

Gabriel Gouvea - Global Solution Java FIAP 2026
