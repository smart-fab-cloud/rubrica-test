package rubrica.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeleteTest {
    
    private WebTarget rubrica;
    public DeleteTest()  { 
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        rubrica = cli.target("http://localhost:50001/rubrica");
    }    
    
    @Test
    public void testDeleteNotAllowed() {
        // Richiesta di Delete sulla risorsa principale
        Response rDelete = rubrica.request().delete();
        
        // Verifica che la risposta sia "405 Not Allowed"
        assertEquals(405, rDelete.getStatus());
    }
    
}
