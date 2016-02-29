import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

/**
 * @author Andrii Iakovenko
 *
 */
public class GenerateKeyPairs {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter password (optional): ");
		String password = br.readLine();

		// Generate generate public/private key pair
		KeyPair keyPair = null;
		if (StringUtils.isBlank(password)) {
			// Privite key wouldn't be secured with password
			keyPair = KeyPairGenerator.generate();
		} else {
			// Privite key will be secured with password
			keyPair = KeyPairGenerator.generate(password);
		}

		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		System.out.println(String.format("Public Key: \n%1$s", new String(publicKey.getEncoded())));
		System.out.println(String.format("Private Key: \n%1$s", new String(privateKey.getEncoded())));
	}

}
