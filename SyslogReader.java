import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyslogReader {

    // Estrutura de dados escolhida
    private static ArrayList<EventoRede> eventos = new ArrayList<>();

    // Classe que representa um evento de rede
    static class EventoRede {
        private String timestamp;
        private String mensagem;

        public EventoRede(String timestamp, String mensagem) {
            this.timestamp = timestamp;
            this.mensagem = mensagem;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getMensagem() {
            return mensagem;
        }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + mensagem;
        }
    }

    // Regex para identificar eventos de rede
    private static final Pattern networkPattern = Pattern.compile(
            "(?i)(NetworkManager|wpa_supplicant|dhcp|eth\\d|wlan\\d|connected|disconnected|link up|link down)"
    );

    public static void main(String[] args) {

        String filePath = "/var/log/syslog";

        if (args.length > 0) {
            filePath = args[0];
        }

        // Lê os eventos do arquivo
        carregarEventos(filePath);

        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n==========================================");
            System.out.println("       GERENCIADOR DE EVENTOS DE REDE");
            System.out.println("==========================================");
            System.out.println("1 - Adicionar evento");
            System.out.println("2 - Remover evento");
            System.out.println("3 - Listar todos os eventos");
            System.out.println("4 - Exportar eventos para CSV");
            System.out.println("5 - Sair");
            System.out.println("==========================================");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {

                    case 1:
                        adicionarEvento(scanner);
                        break;

                    case 2:
                        removerEvento(scanner);
                        break;

                    case 3:
                        listarEventos();
                        break;

                    case 4:
                        exportarCSV(scanner);
                        break;

                    case 5:
                        System.out.println("Programa encerrado.");
                        break;

                    default:
                        System.out.println("Opção inválida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
                opcao = 0;
            }

        } while (opcao != 5);

        scanner.close();
    }

    // ==========================================
    // CARREGAR EVENTOS DO SYSLOG
    // ==========================================

    private static void carregarEventos(String filePath) {

        System.out.println("Lendo arquivo: " + filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {

                Matcher matcher = networkPattern.matcher(line);

                if (matcher.find()) {

                    String timestamp;

                    if (line.length() >= 15) {
                        timestamp = line.substring(0, 15);
                    } else {
                        timestamp = "Data/Hora N/A";
                    }

                    // Adiciona o evento na estrutura
                    eventos.add(new EventoRede(timestamp, line));
                }
            }

            System.out.println("Eventos carregados: " + eventos.size());

        } catch (IOException e) {

            System.err.println(
                    "Erro ao ler o arquivo syslog: " + e.getMessage()
            );
        }
    }

    // ==========================================
    // ADICIONAR EVENTO
    // ==========================================

    private static void adicionarEvento(Scanner scanner) {

        System.out.println("\n=== ADICIONAR EVENTO ===");

        System.out.print("Digite a data/hora do evento: ");
        String timestamp = scanner.nextLine();

        System.out.print("Digite a descrição do evento: ");
        String mensagem = scanner.nextLine();

        EventoRede novoEvento = new EventoRede(timestamp, mensagem);

        eventos.add(novoEvento);

        System.out.println("Evento adicionado com sucesso!");
    }

    // ==========================================
    // REMOVER EVENTO
    // ==========================================

    private static void removerEvento(Scanner scanner) {

        if (eventos.isEmpty()) {
            System.out.println("Não existem eventos para remover.");
            return;
        }

        listarEventos();

        System.out.print("\nDigite o número do evento que deseja remover: ");

        try {

            int indice = Integer.parseInt(scanner.nextLine());

            if (indice >= 1 && indice <= eventos.size()) {

                EventoRede removido = eventos.remove(indice - 1);

                System.out.println("Evento removido:");
                System.out.println(removido);

            } else {

                System.out.println("Número de evento inválido.");
            }

        } catch (NumberFormatException e) {

            System.out.println("Digite um número válido.");
        }
    }

    // ==========================================
    // LISTAR TODOS OS EVENTOS
    // ==========================================

    private static void listarEventos() {

        System.out.println("\n=== EVENTOS REGISTRADOS ===");

        if (eventos.isEmpty()) {

            System.out.println("Nenhum evento registrado.");
            return;
        }

        for (int i = 0; i < eventos.size(); i++) {

            System.out.println(
                    (i + 1) + " - " + eventos.get(i)
            );
        }

        System.out.println("\nTotal de eventos: " + eventos.size());
    }

    // ==========================================
    // EXPORTAR PARA CSV
    // ==========================================

    private static void exportarCSV(Scanner scanner) {

        System.out.println("\n=== EXPORTAR PARA CSV ===");

        System.out.print("Digite o nome do arquivo CSV: ");

        String nomeArquivo = scanner.nextLine();

        if (!nomeArquivo.toLowerCase().endsWith(".csv")) {
            nomeArquivo += ".csv";
        }

        try (PrintWriter writer = new PrintWriter(
                new FileWriter(nomeArquivo))) {

            // Cabeçalho do CSV
            writer.println("Numero,Data_Hora,Mensagem");

            // Dados dos eventos
            for (int i = 0; i < eventos.size(); i++) {

                EventoRede evento = eventos.get(i);

                // Escapa aspas e vírgulas
                String mensagem = evento.getMensagem()
                        .replace("\"", "\"\"");

                writer.println(
                        (i + 1) + ",\"" +
                        evento.getTimestamp() + "\",\"" +
                        mensagem + "\""
                );
            }

            System.out.println(
                    "Arquivo CSV criado com sucesso: " + nomeArquivo
            );

        } catch (IOException e) {

            System.err.println(
                    "Erro ao criar arquivo CSV: " + e.getMessage()
            );
        }
    }
}
