import java.io.BufferedReader;
import java.io.FileReader;

/*
 * Etapa 2 - Interagindo com o syslog
 *
 * Este programa le o arquivo de log do Linux (/var/log/syslog) linha por linha
 * e mostra na tela apenas os eventos de CONEXAO e DESCONEXAO de rede,
 * junto com a data e a hora em que cada evento aconteceu.
 */
public class LeitorSyslog {

    // Palavras que indicam que a rede CONECTOU
    static String[] PALAVRAS_CONEXAO = {
        "Link UP",
        "carrier acquired",
        "CTRL-EVENT-CONNECTED",
        "bound to",
        "state change: config -> ip-config",
        "device (eth0): state change: ip-config -> ip-check",
        "connected",
        "NetworkManager state is now CONNECTED"
    };

    // Palavras que indicam que a rede DESCONECTOU
    static String[] PALAVRAS_DESCONEXAO = {
        "Link DOWN",
        "carrier lost",
        "CTRL-EVENT-DISCONNECTED",
        "DHCPRELEASE",
        "deactivating device",
        "disconnected",
        "NetworkManager state is now DISCONNECTED"
    };

    public static void main(String[] args) {

        // Caminho do syslog. Se o usuario passar um caminho, usamos ele.
        String caminho = "/var/log/syslog";
        if (args.length > 0) {
            caminho = args[0];
        }

        System.out.println("==================================================");
        System.out.println(" EVENTOS DE REDE ENCONTRADOS NO SYSLOG");
        System.out.println(" Arquivo lido: " + caminho);
        System.out.println("==================================================");
        System.out.println();

        int totalLinhas = 0;
        int totalConexoes = 0;
        int totalDesconexoes = 0;

        try {
            // Abre o arquivo para leitura
            BufferedReader leitor = new BufferedReader(new FileReader(caminho));
            String linha = leitor.readLine();

            // Enquanto existir linha para ler...
            while (linha != null) {
                totalLinhas = totalLinhas + 1;

                // Testamos a desconexao primeiro, porque a palavra "disconnected"
                // tem dentro dela a palavra "connected" e isso confundiria o programa.
                if (temPalavra(linha, PALAVRAS_DESCONEXAO)) {
                    mostrarEvento("DESCONEXAO", linha);
                    totalDesconexoes = totalDesconexoes + 1;
                } else if (temPalavra(linha, PALAVRAS_CONEXAO)) {
                    mostrarEvento("CONEXAO   ", linha);
                    totalConexoes = totalConexoes + 1;
                }

                linha = leitor.readLine();
            }

            leitor.close();

        } catch (Exception erro) {
            System.out.println("Nao foi possivel ler o arquivo: " + erro.getMessage());
            System.out.println("Dica: rode com sudo ou passe o caminho do arquivo.");
            System.out.println("Exemplo: java LeitorSyslog syslog");
            return;
        }

        System.out.println();
        System.out.println("==================================================");
        System.out.println(" RESUMO");
        System.out.println(" Linhas lidas no syslog .....: " + totalLinhas);
        System.out.println(" Eventos de conexao .........: " + totalConexoes);
        System.out.println(" Eventos de desconexao ......: " + totalDesconexoes);
        System.out.println(" Total de eventos de rede ...: " + (totalConexoes + totalDesconexoes));
        System.out.println("==================================================");
    }

    // Verifica se a linha contem alguma das palavras da lista
    static boolean temPalavra(String linha, String[] palavras) {
        for (int i = 0; i < palavras.length; i++) {
            if (linha.contains(palavras[i])) {
                return true;
            }
        }
        return false;
    }

    // Mostra o evento formatado na tela
    static void mostrarEvento(String tipo, String linha) {
        System.out.println("[" + tipo + "] " + pegarDataHora(linha));
        System.out.println("             " + pegarMensagem(linha));
        System.out.println();
    }

    /*
     * No syslog do Linux os 15 primeiros caracteres da linha sao a data e a hora.
     * Exemplo: "Sep  5 11:34:01 ubuntu NetworkManager[812]: ..."
     *           |-- 15 caracteres --|
     */
    static String pegarDataHora(String linha) {
        if (linha.length() >= 15) {
            return linha.substring(0, 15);
        }
        return linha;
    }

    // Pega o texto do evento (o que vem depois do nome do programa e dos ":")
    static String pegarMensagem(String linha) {
        int posicao = linha.indexOf("]: ");
        if (posicao > 0) {
            return linha.substring(posicao + 3);
        }
        if (linha.length() > 15) {
            return linha.substring(15).trim();
        }
        return linha;
    }
}
