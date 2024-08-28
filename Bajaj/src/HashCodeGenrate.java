   
import java.io.IOException;
	import java.nio.file.Files;
	import java.nio.file.Paths;
	import java.security.MessageDigest;
	import java.security.NoSuchAlgorithmException;
	import java.util.Random;
	import java.util.Stack;
public class HashCodeGenrate {
	

	
	    public static void main(String[] args) {
	        if (args.length != 2) {
	            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <JSON File Path>");
	            return;
	        }

	        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
	        String jsonFilePath = args[1];

	        try {
	            // Step 2: Read JSON file content
	            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

	            // Step 3: Traverse JSON and find the first instance of "destination"
	            String destinationValue = findDestinationValue(content);

	            if (destinationValue != null) {
	                // Step 4: Generate a random 8-character alphanumeric string
	                String randomString = generateRandomString(8);

	                // Step 5: Generate MD5 hash of PRN + destinationValue + randomString
	                String concatenatedValue = prnNumber + destinationValue + randomString;
	                String hash = generateMD5Hash(concatenatedValue);

	                // Step 6: Print the output in the format <hashed value>;<random string>
	                System.out.println(hash + ";" + randomString);
	            } else {
	                System.out.println("The key 'destination' was not found in the JSON file.");
	            }

	        } catch (IOException | NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	    }

	    private static String findDestinationValue(String jsonContent) {
	        Stack<Character> stack = new Stack<>();
	        boolean insideQuotes = false;
	        StringBuilder keyBuilder = new StringBuilder();
	        StringBuilder valueBuilder = new StringBuilder();
	        boolean isKey = true;

	        for (int i = 0; i < jsonContent.length(); i++) {
	            char currentChar = jsonContent.charAt(i);

	            if (currentChar == '"') {
	                insideQuotes = !insideQuotes;
	                if (!insideQuotes && isKey && keyBuilder.toString().equals("destination")) {
	                    isKey = false;
	                }
	                continue;
	            }

	            if (insideQuotes) {
	                if (isKey) {
	                    keyBuilder.append(currentChar);
	                } else {
	                    valueBuilder.append(currentChar);
	                }
	            } else {
	                if (currentChar == '{' || currentChar == '[') {
	                    stack.push(currentChar);
	                } else if (currentChar == '}' || currentChar == ']') {
	                    stack.pop();
	                }

	                if (!insideQuotes && stack.isEmpty() && !isKey) {
	                    return valueBuilder.toString();
	                }

	                if (currentChar == ':' && !insideQuotes) {
	                    isKey = false;
	                }

	                if (currentChar == ',' || currentChar == '}' || currentChar == ']') {
	                    keyBuilder.setLength(0);
	                    valueBuilder.setLength(0);
	                    isKey = true;
	                }
	            }
	        }
	        return null;
	    }

	    private static String generateRandomString(int length) {
	        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	        Random random = new Random();
	        StringBuilder stringBuilder = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {
	            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
	        }
	        return stringBuilder.toString();
	    }

	    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] digest = md.digest(input.getBytes());
	        StringBuilder sb = new StringBuilder();
	        for (byte b : digest) {
	            sb.append(String.format("%02x", b));
	        }
	        return sb.toString();
	    }
	}


