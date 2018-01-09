package inventario.numero;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class PostTest {
    
    private final WebTarget rubrica;
    private final String cognome;
    private final String nome;
    private final String numero;
    
    public PostTest() {
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        rubrica = cli.target("http://localhost:50001/rubrica");
        // Inizializzazione dati del test
        this.cognome = "Rossi";
        this.nome = "Mario";
        this.numero = "+39050565758";
    }
    
    @Before
    public void aggiuntaProdotto() {
        rubrica.queryParam("cognome", cognome)
                .queryParam("nome", nome)
                .queryParam("numero", numero)
                .request()
                .post(Entity.entity("", MediaType.TEXT_PLAIN));
    }
    
    @Test
    public void testPostNotAllowed() {
        Response rPost = rubrica.path(cognome)
                            .path(nome)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        
        assertEquals(405, rPost.getStatus());
    }
    
    @After
    public void eliminazioneProdotto() {
        rubrica.path(cognome).path(nome).request().delete();
    }
}
