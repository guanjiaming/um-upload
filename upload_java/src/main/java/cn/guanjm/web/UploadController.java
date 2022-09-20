package cn.guanjm.web;

import cn.guanjm.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("file")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestBody MultipartFile file) {
        Map<String, String> map = new HashMap<>();
        map.put("url", uploadService.uploadFile(file));
        map.put("status", "success");
        return ResponseEntity.ok(map);
    }

    @GetMapping("verify/{md5hash}")
    public ResponseEntity<String> verifyFile(@PathVariable("md5hash") String md5hash) {
        return ResponseEntity.ok(uploadService.verifyFile(md5hash));
    }
}
