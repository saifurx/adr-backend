package com.kasa.adr.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class ImagePlaceholderProcessor {

    public static void main(String[] args) {
        String imageUrl = "https://fastly.picsum.photos/id/70/200/200.jpg?hmac=hRU7tEHltyLUTf0bCrAWFXlPRXOBTsvCcvL-dIUG2CE";
        String outputPath = "output.png";
        
        try {
            // Create a template image with a placeholder
            BufferedImage template = createTemplateWithPlaceholder(800, 600);
            
            // Download the image to insert
            BufferedImage replacementImage = downloadImage(imageUrl);
            
            // Replace the placeholder
            BufferedImage result = replacePlaceholder(template, replacementImage, 
                new Rectangle(100, 100, 600, 400)); // x,y,width,height of placeholder
            
            // Save the result
            ImageIO.write(result, "png", new File(outputPath));
            System.out.println("Image created: " + outputPath);
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static BufferedImage downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    public static BufferedImage createTemplateWithPlaceholder(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Draw a background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        // Draw a placeholder rectangle
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(100, 100, 600, 400);
        g.setColor(Color.BLACK);
        g.drawString("[IMAGE PLACEHOLDER]", 350, 300);
        
        g.dispose();
        return image;
    }

    public static BufferedImage replacePlaceholder(BufferedImage template, 
            BufferedImage replacement, Rectangle placeholderArea) {
        
        BufferedImage result = new BufferedImage(
            template.getWidth(), template.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = result.createGraphics();
        
        // Draw the original template
        g.drawImage(template, 0, 0, null);
        
        // Draw the replacement image scaled to fit the placeholder area
        g.drawImage(replacement, 
            placeholderArea.x, placeholderArea.y, 
            placeholderArea.width, placeholderArea.height, null);
        
        g.dispose();
        return result;
    }
}