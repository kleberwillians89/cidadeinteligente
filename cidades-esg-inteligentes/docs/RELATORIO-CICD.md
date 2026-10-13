# Revisão CI/CD — 13/10/2026

## 1. Problemas encontrados

- O Git já apresentava exclusão do workflow antigo e `.github/` não rastreada. A movimentação estava iniciada, mas o YAML na raiz ainda executava Maven/Docker/scripts e localizava artefatos como se o projeto estivesse na raiz.
- Cache Maven não especificava o POM na subpasta; uploads e downloads usavam caminhos incompatíveis com ela.
- Compose combina listas de portas: os overrides podiam preservar 8080 junto de 8081/8082. `APP_PORT=8080` do exemplo também anulava os defaults do script.
- Smoke test usava `curl | tee` com shell POSIX sem `pipefail`, podendo concluir com sucesso após falha funcional. Não validava JSON/status nem ranking nos dois ambientes.
- Senhas de CI estavam fixas no YAML; Compose aceitava senha padrão silenciosamente.
- README mostrava estrutura antiga e checklist que misturava configuração com execução comprovada.

## 2. Correções realizadas

- `.github/workflows/ci-cd.yml`: workflow na raiz, comandos na subpasta com Bash/pipefail, cache pelo POM correto, caminhos explícitos de upload/download, artefato de imagem obrigatório, timeout dos jobs e deploy apenas na main fora de PR. Sequência build/testes → staging → produção mantida. A mesma tag SHA/imagem salva é carregada nos dois ambientes.
- Workflow: senha aleatória mascarada por job, respostas e diagnósticos coletados antes do upload e limpeza mesmo em falhas. A senha temporária fica no ambiente do job para permitir diagnóstico/limpeza.
- `docker-compose.yml`: senha obrigatória no `.env` ou ambiente; persistência, rede interna e dependência do health do banco preservadas.
- Overrides staging/production: `!override` substitui portas, fixando 8081/8082; nomes dos bancos e URLs distintos. Requer Compose >= 2.24.4. Projetos separados isolam volumes/redes/containers.
- `scripts/deploy-environment.sh`: resolve diretório do projeto, fixa porta do ambiente, aguarda saúde com timeout de 180 segundos. Scripts agora executáveis.
- `scripts/smoke-test.sh`: curl com timeout e saída direta em arquivo; falhas propagadas; valida JSON, health/readiness UP e estrutura das listas de cidades/ranking. Produção sem seed aceita lista vazia. Requer Python 3/curl.
- `.env.example`, `.gitignore`, README e guia de evidências: instruções atualizadas, arquivos locais/arquivo de imagem ignorados e checklist com pendências reais.
- Dockerfile multi-stage, POM, código Java, testes, migrações e PDF original preservados, sem alterações.

A substituição das portas segue a [documentação oficial de merge do Docker Compose](https://docs.docker.com/reference/compose-file/merge/). O cache pelo POM usa `cache-dependency-path`, conforme [setup-java](https://github.com/actions/setup-java).

## 3. Validações locais

| Verificação/comando | Resultado |
|---|---|
| `Psych.parse_file` (Ruby) no workflow e três arquivos Compose | Sintaxe YAML aprovada, inclusive tag `!override` |
| Inspeções estruturais com `YAML.load_file` | Diretório/shell, caminhos das actions, dependências de jobs e portas/bancos esperados aprovados; não substitui actionlint/Compose |
| `sh -n scripts/deploy-environment.sh` e `sh -n scripts/smoke-test.sh` | Aprovados |
| Smoke test com curl simulado em diretório temporário | 8 cenários aprovados: sucesso/lista vazia, status DOWN, erro HTTP, JSON inválido, resposta sem lista, cidade malformada, MAX_ATTEMPTS zero e não numérico. Cada erro retornou código não zero |
| Tentativa de servidor HTTP local para smoke test | Bloqueada pelo sandbox ao abrir socket; testes posteriores usaram curl simulado. API real não foi testada |
| `java -version` | Launcher presente, runtime Java ausente |
| `mvn clean verify` | Não executou: Maven não instalado (exit 127) |
| `docker build -t cidades-esg:validation .` | Não executou: Docker não instalado (exit 127) |
| `docker compose config --quiet`, base e cada override | Não executaram: Docker não instalado (exit 127) |
| `git diff --check` | Aprovado |
| `git diff -- src pom.xml` e PDF | Sem alterações nesses arquivos |
| Varredura textual de chaves privadas/tokens GitHub/AWS conhecidos | Sem ocorrências; nenhum `.env` real versionado. Valores existentes são exemplos locais, não credenciais fornecidas pelo usuário. A varredura não garante detecção de todos os formatos de segredo |

Não houve execução atual dos testes Java, integração com PostgreSQL, construção real da imagem ou smoke test contra a aplicação. Evidências antigas foram preservadas e não comprovam esta revisão.

## 4. Pendências

1. Executar validação completa com Java 21/Maven/Docker Compose >= 2.24.4, usando os comandos do README; subir ambos os ambientes e verificar isolamento/saúde.
2. Publicar e executar o workflow na main; confirmar três jobs verdes, artefatos e ausência de erro em diagnóstico/limpeza. A sintaxe YAML passou; actionlint não está instalado.
3. Configurar/revisar GitHub Environments e eventuais reviewers conforme disponibilidade da conta. Nenhum secret foi alterado.
4. Preencher nomes/RMs, anexar prints reais, revisar PDF e gerar ZIP atualizado. Não foi afirmado sucesso remoto.
5. Os containers no GitHub são ambientes efêmeros de homologação, inclusive o job denominado produção. Não oferecem hospedagem permanente.

## 5. Publicação

Nenhum commit, push ou deploy remoto foi executado. A branch local observada é `main`. Execute a partir da raiz, após revisar os arquivos:

```bash
cd '/Users/k/Downloads/CIDADE INTELIGENTE'
git status --short
git diff --check
git diff
cat .github/workflows/ci-cd.yml
git add .github/workflows/ci-cd.yml cidades-esg-inteligentes/.github/workflows/ci-cd.yml cidades-esg-inteligentes/.gitignore cidades-esg-inteligentes/.env.example cidades-esg-inteligentes/README.md cidades-esg-inteligentes/docker-compose.yml cidades-esg-inteligentes/docker-compose.staging.yml cidades-esg-inteligentes/docker-compose.production.yml cidades-esg-inteligentes/scripts/deploy-environment.sh cidades-esg-inteligentes/scripts/smoke-test.sh cidades-esg-inteligentes/docs/GUIA-EVIDENCIAS.md cidades-esg-inteligentes/docs/RELATORIO-CICD.md
git diff --cached --check
git diff --cached
git commit -m "fix: corrigir caminhos CI/CD e validar ambientes isolados"
git push origin main
```

## 6. Evidências FIAP após execução

Capturar URL do run, SHA/data e prints legíveis de:

- Actions com os três jobs verdes e dependências visíveis.
- Maven mostrando contagem real dos testes, zero falhas/erros e BUILD SUCCESS.
- Build Docker e carga da mesma tag SHA em staging/produção.
- Logs do deploy com portas 8081/8082, containers saudáveis e smoke test aprovado.
- Artefatos dos dois ambientes: health/readiness UP, cities/ranking, estado e logs dos containers.
- Deployments/Environments com staging e production; esclarecer na entrega que são efêmeros.

Somente após conferir as evidências, atualizar checklist, README, PDF e ZIP. Roteiro em `GUIA-EVIDENCIAS.md`.
