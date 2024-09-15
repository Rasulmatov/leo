package uz.universes.leotelegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.VerbatimFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.universes.leotelegrambot.Model.*;
import uz.universes.leotelegrambot.config.RequestUrl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {
private final RequestUrl requestUrl;
    public void sendPhone(String phone) {
        PhoneNumber phoneNumber= PhoneNumber.builder().phone(phone).build();
        ObjectMapper objectMapper=new ObjectMapper();
        try {
        String s=objectMapper.writeValueAsString(phoneNumber);
            HttpClient connection=HttpClient.newHttpClient();
            HttpRequest request=HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(s))
                    .uri(requestUrl.getSms())
                    .header("Content-Type","application/json")
                    .build();
            connection.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Verify verifyPhone(CheckSMS sms){
        ObjectMapper objectMapper=new ObjectMapper();
        Verify verify=null;
        try {
            String requestCode = objectMapper.writeValueAsString(sms);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestCode))
                    .uri(requestUrl.getVerify())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
                verify = objectMapper.readValue(send.body(), Verify.class);


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return verify;
    }

    public Boolean checkUser(Long chatId) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(requestUrl.getCheckUser().toString() + chatId + "/"))
                    .GET()
                    .header("Accept","application/json")
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
                UserCheck userCheck=objectMapper.readValue(send.body(),UserCheck.class);
            return userCheck.getSuccess();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveUser(Long chatid , UserEntity entity) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            String json=objectMapper.writeValueAsString(entity);
            HttpClient client=HttpClient.newHttpClient();
            HttpRequest request= HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type","application/json")
                    .uri(new URI(requestUrl.getSaveUser().toString()+chatid+"/"))
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (send.statusCode()!=200){
                log.warn("Save bolishda xatolik statusCod: "+send.statusCode());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public Verify saveBonus(String code , Bonus bonus) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            String json=objectMapper.writeValueAsString(bonus);
            HttpClient client=HttpClient.newHttpClient();
            HttpRequest request= HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type","application/json")
                    .uri(new URI(requestUrl.getBonusSave().toString()+code+"/"))
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (send.statusCode()!=200){
                log.warn("Save bolishda xatolik statusCod: "+send.statusCode());
            }
            Verify verify=objectMapper.readValue(send.body(),Verify.class);
            return verify;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public void patchUser(Long chatid , UserPatch entity) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            String json=objectMapper.writeValueAsString(entity);
            HttpClient client=HttpClient.newHttpClient();
            HttpRequest request= HttpRequest.newBuilder()
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type","application/json")
                    .uri(new URI(requestUrl.getPatchUser().toString()+chatid+"/"))
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (send.statusCode()!=200){
                log.warn("Save bolishda xatolik statusCod: "+send.statusCode());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Region> getRegions() {
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(requestUrl.getRegions(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        List<Region> regions = null;
        try {
            regions = mapper.readValue(jsonResponse, new TypeReference<List<Region>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regions;
    }

    public UsersDto getUsers(Long chatId,String url){
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(url+chatId.toString()+"/"))
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            UsersDto usersDto=objectMapper.readValue(send.body(),UsersDto.class);
            return usersDto;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public UsersDto getUsers(Long chatId){
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(requestUrl.getGetUserUz().toString()+chatId.toString()+"/"))
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            UsersDto usersDto=objectMapper.readValue(send.body(),UsersDto.class);
            return usersDto;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public CheckCod checkCode(String code){
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(requestUrl.getCheckCode().toString()+code+"/"))
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            CheckCod cod=objectMapper.readValue(send.body(),CheckCod.class);
            return cod;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Info info() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(requestUrl.getInfoConnactions().toString()))
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(send.body(), Info.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }






}
