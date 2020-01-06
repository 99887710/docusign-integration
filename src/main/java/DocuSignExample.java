import com.amazonaws.util.Base64;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.model.*;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class DocuSignExample {
    public static void main(String[] args) throws Exception {
        // Data for this example
        // Fill in these constants
        //
        // Obtain an OAuth access token from https://developers.hqtest.tst/oauth-token-generator
        String accessToken = "YourAccessToken"
        // Obtain your accountId from demo.docusign.com -- the account id is shown in the drop down on the
        // upper right corner of the screen by your picture or the default picture.
        String accountId = "YourAccountId";
        // Recipient Information
        String signerName = "Bob";
        String signerEmail = "Bob.Brown@gmail.com";

        // The url for this web application
        String baseUrl = "http://localhost:8080";
        //
        // The API base path
        String basePath = "https://demo.docusign.net/restapi";
        // The document to be signed. See /qs-java/src/main/resources/World_Wide_Corp_lorem.pdf
        String docPdf = "data/World_Wide_Corp_lorem.pdf";

        // Step 1. Create the envelope definition
        // One "sign here" tab will be added to the document.

//        byte[] buffer = readFile(docPdf);
        byte[] buffer = Files.readAllBytes(Paths.get(docPdf));
        String docBase64 = new String(Base64.encode(buffer));

        // Create the DocuSign document object
        Document document = new Document();
        document.setDocumentBase64(docBase64);
        document.setName("Example document"); // can be different from actual file name
        document.setFileExtension("pdf"); // many different document types are accepted
        document.setDocumentId("1"); // a label used to reference the doc

        // The signer object
        // Create a signer recipient to sign the document, identified by name and email
        Signer signer = new Signer();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.recipientId("1");

        // Create a signHere tabs (also known as a field) on the document,
        // We're using x/y positioning. Anchor string positioning can also be used
        SignHere signHere = new SignHere();
        signHere.setDocumentId("1");
        signHere.setPageNumber("1");
        signHere.setRecipientId("1");
        signHere.setTabLabel("SignHereTab");
        signHere.setXPosition("195");
        signHere.setYPosition("147");

        // Add the tabs to the signer object
        // The Tabs object wants arrays of the different field/tab types
        Tabs signerTabs = new Tabs();
        signerTabs.setSignHereTabs(Arrays.asList(signHere));
        signer.setTabs(signerTabs);

        // Next, create the top level envelope definition and populate it.
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setDocuments(Arrays.asList(document));
        // Add the recipient to the envelope object
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer));
        envelopeDefinition.setRecipients(recipients);
        envelopeDefinition.setStatus("sent"); // requests that the envelope be created and sent.

        // Step 2. Call DocuSign to create and send the envelope
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        EnvelopeSummary results = envelopesApi.createEnvelope(accountId, envelopeDefinition);
        String envelopeId = results.getEnvelopeId();

        System.out.println("envelopeId: " + envelopeId);

        // Show results
//        String title = "Signing Request by Email";
//        model.addAttribute("title", title);
//        model.addAttribute("h1", title);
//        model.addAttribute("message", "Envelopes::create results");
//        model.addAttribute("json", new JSONObject(results).toString(4));
//        return "pages/example_done";

    }

}
