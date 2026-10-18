# RokuControl

Controle remoto Android nativo para Roku TV, operando na rede local via SSDP (descoberta) e
Roku External Control Protocol (ECP, HTTP na porta 8060). Projeto independente de qualquer canal
Roku e sem BrightScript.

Validado contra uma SEMP Roku TV 43R6500 (Roku OS 16.0.4, tuner ISDB-T).

## Stack

- Kotlin, Jetpack Compose (Material 3)
- `minSdk` 24 (Android 7.0), `compileSdk`/`targetSdk` 36 (Android 16)
- OkHttp (cliente ECP), Coroutines/StateFlow
- Build com Gradle 8.11.1 / AGP 8.9.1 / Kotlin 2.1.20

## Arquitetura

| Camada | Componente | Responsabilidade |
|---|---|---|
| Rede | `net/SsdpDiscovery` | Descoberta multicast (M-SEARCH `roku:ecp`) com `MulticastLock` |
| Rede | `net/EcpClient` | Comandos e consultas ECP; retry em falha de rede |
| Rede | `net/RokuXml` | Parsing dos XML da ECP; sem dependência Android (testável em JVM) |
| Rede | `net/WakeOnLan` | Magic packet para ligar a TV em standby |
| Dados | `data/RokuRepository` | Orquestra ECP e SSDP; persiste o último host |
| Estado | `ui/RemoteViewModel` | `AndroidViewModel`, estado por `StateFlow`, callbacks de erro |
| UI | `ui/*` | Compose: conexão, controle, apps, TV ao vivo, teclado |

Operações de rede rodam em `Dispatchers.IO`; a UI não bloqueia.

## Funcionalidades

- Descoberta na rede (SSDP) e entrada manual de IP, com reconexão ao último host (fallback)
- Direcional, OK, Voltar, Início, opções
- Reprodução: play/pause, avançar, retroceder, repetição instantânea, info
- Volume, mudo e desligar; ligar via Wake-on-LAN
- Troca de entrada: TV ao vivo (tuner), HDMI 1-3, AV; canais acima/abaixo
- Lista de apps instalados, carregada dinamicamente, com destaque do app ativo e ícones
- Lista de canais de TV ao vivo
- Entrada de texto remota (campos de teclado da TV) e acionamento de busca por voz
- Feedback de erro por callback (mensagem quando um comando não chega à TV)

## Matriz de capacidades

Legenda: Testado = verificado nesta TV; Documentado = previsto no protocolo, não validado botão a botão;
Nao suportado = indisponível neste hardware; Requer RE = fora da ECP, depende de engenharia reversa.

| Recurso | Metodo ECP | Situacao |
|---|---|---|
| Descoberta | SSDP `ST roku:ecp` | Testado |
| Informacoes do dispositivo | `GET /query/device-info` | Testado |
| Lista de apps | `GET /query/apps` | Testado |
| App ativo | `GET /query/active-app` | Testado |
| Icones de apps | `GET /query/icon/<id>` | Documentado |
| Direcional, OK, Voltar, Inicio | `POST /keypress/{Up,Down,Left,Right,Select,Back,Home}` | Testado |
| Reproducao | `POST /keypress/{Play,Rev,Fwd,InstantReplay,Info}` | Documentado |
| Volume e mudo | `POST /keypress/{VolumeUp,VolumeDown,VolumeMute}` | Documentado |
| Desligar | `POST /keypress/PowerOff` | Documentado |
| Ligar | Wake-on-LAN (magic packet) | Documentado |
| Troca de canal | `POST /keypress/{ChannelUp,ChannelDown}` | Documentado |
| Entradas | `POST /keypress/{InputTuner,InputHDMI1..3,InputAV1}` | Documentado |
| Abrir app | `POST /launch/<id>` | Documentado |
| Entrada de texto | `POST /keypress/Lit_<char>` | Documentado |
| Enter, Backspace, Busca/voz | `POST /keypress/{Enter,Backspace,Search}` | Documentado |
| Lista de TV ao vivo | `GET /query/tv-channels` | Testado |
| Find Remote | `POST /keypress/FindRemote` | Nao suportado (`supports-find-remote=false`) |
| Audio no celular (private listening) | Stream SAS | Requer RE (fora da ECP) |

Enviar um comando sem erro HTTP nao prova o efeito na tela; itens "Documentado" aguardam validacao
botao a botao.

## Pre-requisitos da TV

Em Ajustes, Sistema, Configuracoes avancadas, Controle por apps de rede deve estar habilitado
(obrigatorio a partir do Roku OS 14.1 para `keypress`, `query/tv-channels` e `query/icon`).

## Build

Toolchain em container (Android SDK API 36 + JDK 21 + Gradle). Debug:

    ./gradlew assembleDebug testDebugUnitTest

Release assinado (requer `keystore.properties` e o keystore locais, nao versionados):

    ./gradlew assembleRelease

Saidas em `app/build/outputs/apk/`.

### Assinatura

Crie `keystore.properties` na raiz (fora do controle de versao):

    storeFile=keystore/rokucontrol.jks
    storePassword=...
    keyAlias=rokucontrol
    keyPassword=...

Sem esse arquivo, `assembleRelease` gera um APK nao assinado.

## Instalacao

1. Baixe o APK da release ou gere com `assembleRelease`.
2. Copie para o aparelho e abra; permita "instalar apps desconhecidos" quando solicitado.
3. Ao trocar entre debug e release, desinstale a versao anterior (assinatura diferente).
4. Conecte o celular na mesma rede da TV, abra o app e use "Procurar na rede" ou informe o IP.

## Permissoes

`INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `CHANGE_WIFI_MULTICAST_STATE` (SSDP),
`VIBRATE` (retorno tatil). Trafego apenas na rede local; nenhum dado sai para servicos externos.

## Limitacoes

- Ajustes da TV nao tem endpoint ECP dedicado; sao acessados navegando a partir do botao Inicio.
- Ligar a TV depende de Wake-on-LAN ativo e do MAC do aparelho.
- A ECP e HTTP em texto claro; a configuracao de seguranca de rede libera apenas esse trafego local.
- Private listening nao e documentado na ECP e nao esta implementado.

## Licenca

Uso pessoal.
