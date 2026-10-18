# RokuControl

App Android nativo (Kotlin/Compose) que controla uma Roku TV via SSDP + ECP (HTTP porta 8060).
Package: `com.nathalia.rokuremote`. Nao e canal Roku, sem BrightScript.

## Ambiente / build
- Build NAO roda no host: usa dev_box (Docker). Imagem `dev_box/controle_remoto`
  (eclipse-temurin:21-jdk-noble + Android SDK cmdline-tools, build-tools 36.0.0, platforms android-36).
  Dockerfile/compose em `C:\DEV_BOX_DEPENDENCIAS\projects\controle_remoto\`.
- JDK 21 no container compila para bytecode 17 (`kotlinOptions.jvmTarget=17` + compileOptions VERSION_17);
  NAO usar `kotlin { jvmToolchain(17) }` (so ha JDK21 na imagem).
- APK sai em volume nomeado; copiar para a raiz bind-montada com `docker cp`/`cp` para ficar visivel no host.
- assembleRelease com R8 estoura memoria junto com testes: rodar testes no debug e release separado,
  `--max-workers=2`.

## Assinatura / segredos
- Release assinado via `keystore.properties` + keystore locais (storeFile=keystore/rokucontrol.jks).
  NUNCA versionar: keystore/, keystore.properties, *.jks, *.apk (ja no .gitignore).

## TV de referencia
- SEMP Roku TV 43R6500, Roku OS 16.0.4, IP 192.168.0.2. "Controle por apps de rede" habilitado.
- `supports-find-remote=false`. Private listening (audio no celular) NAO e ECP: exige RE do APK oficial Roku.
- NUNCA remover/reinstalar o app Twitch custom (id "dev") nem mexer no Stitch (id "817397") sem confirmacao.

## Repo
- GitHub: https://github.com/nathaliarx/controle-remoto-roku (branch main). Release v1.0.0 com APK anexado.
