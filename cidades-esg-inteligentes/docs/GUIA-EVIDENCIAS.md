# Guia rápido para concluir as evidências no GitHub

Repositório: https://github.com/kleberwillians89/cidadeinteligente, branch `main`. Execute este roteiro após publicar a correção; a revisão local não executou jobs remotos.

## 1. Preparar os ambientes

1. Abra **Settings > Environments**.
2. Crie `staging` e `production` com esses nomes exatos.
3. Em `production`, se a conta permitir, ative **Required reviewers** e selecione um integrante.

## 2. Executar o pipeline

1. Envie o código para a branch `main` ou abra **Actions**.
2. Selecione **CI/CD - Cidades ESG Inteligentes**.
3. Clique em **Run workflow**.
4. Aguarde `Build e testes`, `Deploy em staging` e `Deploy em produção` ficarem verdes.
5. Se produção estiver protegida, aprove o job quando o GitHub solicitar.

## 3. Tirar os prints

Capture, sem recortar os nomes dos jobs:

1. resumo do workflow com os três jobs verdes;
2. log da etapa `Compilar e executar testes` mostrando `Tests run: 7` e `BUILD SUCCESS`;
3. log `Subir staging e executar smoke test` mostrando health check aprovado;
4. log `Subir produção e executar smoke test` mostrando health check aprovado;
5. página **Deployments** com os ambientes `staging` e `production`.

## 4. Baixar os artefatos

Na página do run, baixe:

- `relatorios-de-testes`;
- `evidencias-staging`;
- `evidencias-production`.

Os JSONs `health.json` e `readiness.json` devem mostrar `"status": "UP"`. Ambos os ambientes também publicam `cities.json`, `ranking.json`, `deploy.log`, `containers.txt` e `containers.log`. Produção sem seed pode retornar listas vazias. Capture também construção da imagem e carga da mesma tag SHA nos dois jobs. Guarde URL do run, SHA e data com os prints; portas esperadas: staging 8081, produção 8082.

## 5. Atualizar a entrega

1. Coloque os prints em `docs/evidencias/github/`.
2. Adicione os links das imagens à seção **Prints do funcionamento** do `README.md`.
3. Marque `☑` apenas nos requisitos com evidências verificadas.
4. Preencha nome e RM no README e na documentação PDF.
5. Gere novamente o ZIP antes de enviar à plataforma.

> Os deploys do workflow são efêmeros: validam os ambientes em containers do runner e publicam evidências, mas não criam uma URL pública permanente.
