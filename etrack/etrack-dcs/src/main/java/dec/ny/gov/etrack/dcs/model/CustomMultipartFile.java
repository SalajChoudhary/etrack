package dec.ny.gov.etrack.dcs.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

  private byte[] input;
  private String fileName;
  private String contentType;
  
  public CustomMultipartFile(byte[] input, String fileName, String contentType) {
    this.input = input;
    this.fileName = fileName;
    this.contentType = contentType;
  }
  
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getOriginalFilename() {
    return this.fileName;
  }

  @Override
  public String getContentType() {
    return this.contentType;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public long getSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public byte[] getBytes() throws IOException {
    // TODO Auto-generated method stub
    return this.input;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    try (FileOutputStream fos = new FileOutputStream(dest)) {
      fos.write(input);
    }
  }

}
