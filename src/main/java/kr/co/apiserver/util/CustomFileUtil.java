package kr.co.apiserver.util;

import jakarta.annotation.PostConstruct;
import kr.co.apiserver.domain.emums.FileCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    // 썸네일 디렉토리 및 파일 이름 접두사
    private static final String LIST_THUMB_DIR = "list_thumb";
    private static final String LIST_THUMB_PREFIX = "t_";

    private static final String WM_THUMB_DIR = "wm_thumb";
    private static final String WM_THUMB_PREFIX = "w_";

    @PostConstruct // 초기화 메서드
    public void init() {
        List<String> categories = List.of("profile", "product", "product/list_thumb", "product/wm_thumb", "banner");

        for (String category : categories) {
            File categoryFolder = new File(uploadPath, category);
            if (!categoryFolder.exists()) {
                categoryFolder.mkdirs();
                log.info("Created folder: " + categoryFolder.getAbsolutePath() );
            }
        }
    }

    public String saveFile(MultipartFile file, FileCategory category){

        if(file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            return "";
        }
        try {
            // 디렉토리 설정
            Path categoryDir = Paths.get(uploadPath, category.getValue());

            // 저장 파일 이름
            String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path savePath = Paths.get(categoryDir.toString(), savedName);

            // 원본 저장
            Files.copy(file.getInputStream(), savePath);

            String contentType = file.getContentType(); // 파일의 MIME 타입

            // 이미지 파일인 경우 썸네일 생성
            if (contentType != null && contentType.startsWith("image") && category.equals(FileCategory.PRODUCT)) {
                // 리스트용 썸네일 (워터마크 없음)
                // 리스트 썸네일 폴더 경로
                Path listThumbPath = categoryDir.resolve(LIST_THUMB_DIR + "/" + LIST_THUMB_PREFIX + savedName);

                Thumbnails.of(savePath.toFile())
                        .size(400, 400)
                        .keepAspectRatio(true)
                        .toFile(listThumbPath.toFile());

                // 상세페이지용 워터마크 썸네일
                Path wmThumbPath = categoryDir.resolve(WM_THUMB_DIR + "/" + WM_THUMB_PREFIX + savedName);
                BufferedImage originalImage = ImageIO.read(savePath.toFile());
                BufferedImage watermarkImage = ImageIO.read(Paths.get(uploadPath, "watermark.png").toFile());

                BufferedImage watermarkedThumb = Thumbnails.of(originalImage)
                        .size(1000, 1000)
                        .keepAspectRatio(true)
                        .watermark(Positions.CENTER, watermarkImage, 0.5f)
                        .asBufferedImage();

                // 원본 확장자에 맞춰 저장
                String originalExt = savedName.substring(savedName.lastIndexOf('.') + 1).toLowerCase();
                ImageIO.write(watermarkedThumb, originalExt, wmThumbPath.toFile());
            }

            return savedName;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            doDeleteFile("list_thumb/t_" + fileName, category);
            doDeleteFile("wm_thumb/w_" + fileName, category);
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
