package ua.berlinets.file_manager.services;

import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.DTO.FileInformationDTO;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
public class FileMapper {

    public FileInformationDTO fileToDTO(File file) {
        DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return new FileInformationDTO(
                file.getName(),
                file.getPath(),
                file.isFile(),
                file.isDirectory(),
                getFileSize(file.length()),
                sdf.format(file.lastModified())
        );
    }

    private String getFileSize(long fileLength) {
        String fileSize;
        if (fileLength < 1024) {
            fileSize = fileLength + " B";
        } else if (fileLength < 1024 * 1024) {
            fileSize = (fileLength / 1024) + " KB";
        } else if (fileLength < 1024 * 1024 * 1024) {
            fileSize = fileLength / (1024 * 1024) + " MB";
        } else {
            fileSize = fileLength / (1024 * 1024 * 1024) + " GB";
        }
        return fileSize;
    }

}
