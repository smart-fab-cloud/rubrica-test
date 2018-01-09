package rubrica.cognome.nome;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class DeleteTest {
    
    private final WebTarget rubrica;
    private final String cognome;
    private final String nome;
    private final String numero;
    
    public DeleteTest() {
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        rubrica = cli.target("http://localhost:50001/rubrica");
        // Inizializzazione dati del test
        this.cognome = "Rossi";
        this.nome = "Mario";
        this.numero = "+39050565758";
    }
    
    @Before
    public void aggiuntaNumero() {
        rubrica.queryParam("cognome", cognome)
                .queryParam("nome", nome)
                .queryParam("numero", numero)
                .request()
                .post(Entity.entity("", MediaType.TEXT_PLAIN));
    }
    
    @Test
    public void testDeleteOk() throws ParseException {
        Response rDelete = rubrica.path(cognome).path(nome).request().delete();
        
        // Verifica che la risposta sia 200 Ok
        assertEquals(Response.Status.OK.getStatusCode(),rDelete.getStatus());
    }
    
    @Test 
    public void testReperimentoNumeroInesistente() {
        // Reperimento di un numero inesistente
        Response rDelete = rubrica.path(nome+cognome).path(nome+cognome).request().delete();
        
        // Verifica che la risposta sia 404 Not Found
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),rDelete.getStatus());
    }
}
