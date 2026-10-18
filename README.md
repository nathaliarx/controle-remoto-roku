# Controle Roku — Android

Controle remoto Android nativo (Kotlin + Jetpack Compose) para a **SEMP Roku TV 43R6500**,
comunicando pela rede local via **SSDP** (descoberta) e **Roku ECP** (comandos/consultas na porta 8060).
Projeto independente de qualquer canal Roku; não usa BrightScript.

## Dispositivo-alvo (confirmado por ECP + doc oficial)

| Item | Valor | Método |
|---|---|---|
| Modelo | SEMP Roku TV 43R6500 (H109X, região BR) | `query/device-info` |
| Roku OS | 16.0.4 | `query/device-info` |
| Tuner | ISDB-T (TV digital aberta BR) | `query/device-info` |
| Rede | Ethernet, IP 192.168.0.2 | `query/device-info` + SSDP |
| Modo dev | habilitado | `developer-enabled=true` |
| "Controle por apps móveis" | habilitado | `keypress`/`query/tv-channels` responderam 200 |
| Wake-on-WLAN | suportado | `supports-wake-on-wlan=true` |
| Private listening | suportado, **não documentado na ECP** | `audio-device`: `wifi-remote-sas,mobile-sas` |

## Matriz de capacidades

Legenda: ✅ testado nesta TV · 🟡 documentado/anunciado, não testado · ⛔ não suportado · 🔬 exige engenharia reversa

| Recurso | Endpoint/método | Status | Observação |
|---|---|---|---|
| Descoberta SSDP | `M-SEARCH ST roku:ecp` | ✅ | achou 192.168.0.2 |
| Info do dispositivo | `GET /query/device-info` | ✅ | |
| Lista de apps | `GET /query/apps` | ✅ | 15 apps + 5 entradas |
| App ativo | `GET /query/active-app` | ✅ | |
| Ícones de apps | `GET /query/icon/<id>` | 🟡 | carregado na UI em runtime |
| Direcional/OK/Back | `POST /keypress/{Up,Down,Left,Right,Select,Back}` | ✅ | |
| Home | `POST /keypress/Home` | ✅ | |
| Playback | `POST /keypress/{Play,Rev,Fwd,InstantReplay,Info}` | 🟡 | |
| Volume/Mute | `POST /keypress/{VolumeUp,VolumeDown,VolumeMute}` | 🟡 | TV suporta |
| Power Off | `POST /keypress/PowerOff` | 🟡 | |
| Power On | Wake-on-LAN (magic packet) | 🟡 | ECP não liga a TV desligada |
| Trocar canal (tuner) | `POST /keypress/{ChannelUp,ChannelDown}` | 🟡 | |
| Entradas | `POST /keypress/{InputTuner,InputHDMI1..3,InputAV1}` | 🟡 | |
| Abrir app | `POST /launch/<id>` | 🟡 | |
| Digitar texto | `POST /keypress/Lit_<char>` | 🟡 | campos de teclado na tela |
| Enter/Backspace/Busca-voz | `POST /keypress/{Enter,Backspace,Search}` | 🟡 | |
| Lista TV ao vivo | `GET /query/tv-channels` | ✅ | 30 canais ISDB-T |
| Find Remote | `POST /keypress/FindRemote` | ⛔ | `supports-find-remote=false` |
| Ouvir pelo celular | stream SAS (RTP) | 🔬 | fora da ECP; ver seção RE |

> 🟡 = o comando existe no protocolo e a TV deve aceitá-lo, mas ainda não foi validado nesta TV
> botão a botão. Enviar sem erro HTTP não prova o efeito na tela — a validação é a Etapa 6.

## Arquitetura

- `net/SsdpDiscovery` — descoberta multicast.
- `net/EcpClient` — cliente HTTP (OkHttp, corrotinas) dos comandos/queries ECP.
- `net/RokuXml` — parsing dos XML (sem dependência Android, testável em JVM).
- `net/WakeOnLan` — magic packet para ligar a TV.
- `data/RokuRepository` — orquestra ECP + SSDP.
- `ui/RemoteViewModel` — estado (StateFlow), sem bloquear a UI.
- `ui/*` — Compose: conexão, D-pad/playback/volume/entradas, grade de apps, TV ao vivo, teclado. Tema claro/escuro, háptico.

## Build (dev_box)

Pré-requisitos: imagem Android do dev_box (Android SDK + JDK 17 + Gradle). Gerar o wrapper e compilar:

```
devbox.ps1 run controle_remoto bash -c "gradle wrapper --gradle-version 8.11.1 && ./gradlew assembleDebug testDebugUnitTest"
```

APK de depuração: `app/build/outputs/apk/debug/app-debug.apk`.

## Instalar no Moto G (Android 16)

1. Ative "Depuração USB" (Opções do desenvolvedor) ou use "Instalar apps desconhecidos".
2. `adb install -r app/build/outputs/apk/debug/app-debug.apk` (ou copie o APK e abra no aparelho).
3. Conecte o celular na mesma rede Wi-Fi da TV. Abra o app → "Procurar na rede" ou informe `192.168.0.2`.

## Engenharia reversa (private listening)

Ferramentas instaladas em `C:\DEV_BOX_DEPENDENCIAS\tools\re` (JDK 21, jadx 1.5.6, Ghidra 12.1.4, ReVa).
MCPs registrados em escopo de usuário:
- **jadx** (stdio): decompila o APK oficial Roku (Java/Kotlin). Requer jadx-gui aberto com o APK.
- **ReVa** (HTTP :8080): analisa bibliotecas nativas `.so`. Requer Ghidra aberto com a extensão habilitada.

Pendência: obter o **APK oficial do app Roku** para mapear o protocolo de áudio SAS. Sem ele, o recurso
"ouvir pela TV no celular" não pode ser implementado com segurança.

## Limitações conhecidas

- Comandos ECP são política de "não usar em apps de terceiros" da Roku (publicação em loja); em LAN doméstica funcionam. Uso pessoal.
- Power On só via Wake-on-LAN; depende de a TV ter WoL ativo e do MAC (preenchível na config).
- Cleartext HTTP liberado (ECP é HTTP); o app não faz tráfego externo.
