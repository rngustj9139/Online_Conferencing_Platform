package koo.online_education_platform.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
@Builder
public class FileUploadDto {

    private MultipartFile file; // MultipartFile
    private String originFileName; // 파일 원본 이름
    private String transaction; // UUID 를 활용한 랜덤한 파일 위치(파일 이름 중복 방지를 위한 UUID)
    private String chatRoom; // 파일이 올라간 채팅방 ID
    private String s3DataUrl; // 파일 링크
    private String fileDir; // S3 파일 경로

}
