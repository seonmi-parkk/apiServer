package kr.co.apiserver.util;

import jakarta.annotation.PostConstruct;
import kr.co.apiserver.domain.emums.FileCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct // 초기화 메서드
    public void init() {
        List<String> categories = List.of("profile", "product", "product/thumb", "banner");

        for (String category : categories) {
            File categoryFolder = new File(uploadPath, category);
            if (!categoryFolder.exists()) {
                categoryFolder.mkdirs();
                log.info("Created folder: " + categoryFolder.getAbsolutePath() );
            }
        }
    }

    public String saveFile(MultipartFile file, FileCategory category){

        if(file == null || file.isEmpty()) {
            return "";
        }

        Path categoryDir = Paths.get(uploadPath, category.getValue());
        // 썸네일 폴더 경로
        Path thumbDir = categoryDir.resolve("thumb");

        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            return "";
        }

        String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path savePath = Paths.get(categoryDir.toString(), savedName);

        try {
            Files.copy(file.getInputStream(), savePath);  // 원본파일 업로드

            String contentType = file.getContentType(); // 파일의 MIME 타입
            if (contentType != null && contentType.startsWith("image") && category.equals(FileCategory.PRODUCT)) {
                // 이미지 파일인 경우 썸네일 생성
                Path thumbnailPath = thumbDir.resolve("s_" + savedName);

                Thumbnails.of(savePath.toFile())
                        .size(200, 200)
                        .toFile(thumbnailPath.toFile());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return savedName;
    }

    public List<String> saveFiles(List<MultipartFile> files, FileCategory category){

        if(files == null || files.isEmpty()) {
            return List.of();
        }

        List<String> uploadNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                continue;
            }
            // DB에 저장할 경로
            uploadNames.add(saveFile(file, category));
        }
        return uploadNames;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        Path path = Paths.get(uploadPath, fileName);
        Resource resource = new FileSystemResource(path);

        if(!resource.isReadable()) {
            resource = new FileSystemResource(Paths.get( uploadPath,"default.png"));
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void deleteFiles(List<String> fileNames, FileCategory category) {
        if(fileNames == null || fileNames.isEmpty()) {
            return;
        }

        for (String fileName : fileNames) {
            // 단건 파일 deleteFile메서드 추가해서 수정 && 사용하는 부분들 코드 수정해야함
            deleteFile(fileName, category);
        }
    }

    public void deleteFile(String fileName, FileCategory category){
        // 상품 이미지의 경우 썸네일도 삭제
        if(category.equals(FileCategory.PRODUCT)) {
            String thumbFileName = "s_" + fileName;
            doDeleteFile(thumbFileName, category);
        }

        // 원본 파일 삭제
        doDeleteFile(fileName, category);

        // Path thumbnailPath = Paths.get(uploadPath, thumbFileName);
    }

    public void doDeleteFile(String fileName, FileCategory category) {
        Path filePath = Paths.get(uploadPath, category.getValue(), fileName);
        try {
            boolean result = Files.deleteIfExists(filePath);
            log.info("Deleted file: {}, result: {}" ,filePath, result);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
