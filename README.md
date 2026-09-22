# 🌐 Syslog Reader

Gerenciador de eventos de rede desenvolvido em **Java**.

O programa lê um arquivo `syslog`, identifica registros relacionados à rede e
permite gerenciar os eventos por meio de um menu interativo.

## ✨ Funcionalidades

- 🔍 Localizar eventos de rede no syslog
- ➕ Adicionar eventos
- ➖ Remover eventos
- 📋 Listar eventos registrados
- 📄 Exportar os dados para CSV

## 📁 Estrutura

```text
SyslogReader.java  → código-fonte
syslog             → arquivo de exemplo
README.md          → documentação
```

## 🚀 Como executar

Compile o programa:

```bash
javac SyslogReader.java
```

Execute com o arquivo de exemplo:

```bash
java SyslogReader syslog
```

Para utilizar o syslog padrão do Linux:

```bash
sudo java SyslogReader /var/log/syslog
```

## 🖥️ Menu

```text
1 - Adicionar evento
2 - Remover evento
3 - Listar todos os eventos
4 - Exportar eventos para CSV
5 - Sair
```

---

Feito com ☕ e Java.
