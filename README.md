# Etapa 2 - Interagindo com o syslog

Programa em Java que le o syslog do Linux e mostra na tela **somente** os
eventos de conexao e desconexao de rede, com a data e a hora de cada um.

## Arquivos

| Arquivo | O que e |
|---|---|
| `LeitorSyslog.java` | Codigo-fonte do programa |
| `syslog` | Arquivo de syslog de exemplo, para teste |
| `README.md` | Este arquivo |

## Como executar

```bash
javac LeitorSyslog.java      # compila
java LeitorSyslog syslog     # roda usando o arquivo de teste
```

Para ler o syslog real da maquina Linux:

```bash
sudo java LeitorSyslog /var/log/syslog
```

Se nenhum caminho for informado, o programa usa `/var/log/syslog` por padrao.
Em distribuicoes mais novas (que usam journald) pode ser preciso gerar o arquivo antes:

```bash
journalctl --since today > syslog
java LeitorSyslog syslog
```

## Como o programa funciona (passo a passo)

1. Abre o arquivo com `Scanner` e le **uma linha por vez** (nao carrega o
   arquivo inteiro na memoria - o syslog pode ser muito grande).
2. Pega a data e a hora com `linha.substring(0, 15)`, porque no syslog os 15
   primeiros caracteres sao sempre a data e a hora.
3. Usa `linha.contains(...)` para ver se a linha fala de rede:
   - desconexao: `Link DOWN`, `carrier lost`, `DISCONNECTED`, `DHCPRELEASE`
   - conexao: `Link UP`, `carrier acquired`, `CONNECTED`, `bound to`
4. A desconexao e testada **primeiro** de proposito: a palavra `DISCONNECTED`
   contem dentro dela a palavra `CONNECTED`, entao, se testassemos a conexao
   antes, um evento de desconexao seria classificado errado.
5. Se a linha for um evento de rede, imprime data/hora + tipo + a linha.
   As outras linhas sao simplesmente ignoradas.
6. No final mostra quantas conexoes e quantas desconexoes foram encontradas.

## Estrutura do syslog

Cada linha do syslog segue sempre o mesmo formato:

```
Sep  5 08:16:11 ubuntu NetworkManager[812]: <info> device (eth0): Link UP
|-------------| |----| |--------------|     |------------------------------|
   data/hora     host   processo[PID]                  mensagem
```

Por ser um formato fixo, da para pegar a data com um simples `substring(0, 15)`,
sem precisar de biblioteca nenhuma.

Os programas que registram eventos de rede no syslog sao principalmente:
- **kernel** - avisa quando o cabo/placa sobe ou cai (`NIC Link is Up/Down`);
- **NetworkManager** - gerencia as interfaces (`Link UP`, `carrier lost`, estado da rede);
- **dhclient** - pega e devolve o IP via DHCP (`bound to`, `DHCPRELEASE`);
- **wpa_supplicant** - conexao Wi-Fi (`CTRL-EVENT-CONNECTED/DISCONNECTED`).

## Roteiro sugerido para o video (4 min)

- **0:00 - 1:00** Mostrar o arquivo `syslog` aberto e explicar o formato da linha
  (data/hora, host, processo, mensagem) e quais processos geram eventos de rede.
- **1:00 - 2:30** Abrir o `LeitorSyslog.java` e explicar: leitura linha a linha,
  os dois vetores de palavras-chave, o motivo de testar desconexao primeiro,
  e o `substring(0, 15)` que extrai a data/hora.
- **2:30 - 3:30** Compilar e executar mostrando a saida na tela.
- **3:30 - 4:00** Comentar o resumo final e as limitacoes/possiveis melhorias.
