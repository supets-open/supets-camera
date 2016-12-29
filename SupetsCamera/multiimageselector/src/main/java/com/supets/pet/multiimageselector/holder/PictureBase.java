package com.supets.pet.multiimageselector.holder;

import android.database.Cursor;
import android.provider.MediaStore;

import com.supets.commons.utils.UIUtils;
import com.supets.pet.multiimageselector.model.Folder;
import com.supets.pet.multiimageselector.model.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class PictureBase {

    private ArrayList<Folder> mResultFolder = new ArrayList<>();
    private boolean hasFolderGened = false;
    private List<Image> images = new ArrayList<>();

    private void setFolderData() {
        if (images == null) {
            return;
        }
        if (!hasFolderGened) {
            for (Image image : images) {
                // 获取文件夹名称
                File imageFile = new File(image.path);
                if (!imageFile.exists()) {
                    continue;
                }
                File folderFile = imageFile.getParentFile();
                if (!folderFile.exists()) {
                    continue;
                }
                Folder folder = new Folder();
                folder.name = folderFile.getName();
                folder.path = folderFile.getAbsolutePath();
                folder.cover = image;
                if (!mResultFolder.contains(folder)) {
                    List<Image> imageList = new ArrayList<>();
                    imageList.add(image);
                    folder.images = imageList;
                    mResultFolder.add(folder);
                } else {
                    Folder f = mResultFolder.get(mResultFolder
                            .indexOf(folder));
                    f.images.add(image);
                }
            }
            hasFolderGened = true;
        }
    }

     void getPictureData() {
        getAll();
        if (!images.isEmpty()) {
            ArrayList<Image> mthumbs = getThumbList();
            for (Image image : mthumbs) {
                getMergeData(image);
            }
            setFolderData();
        }
    }

    private void getMergeData(Image mthumbs) {
        for (Image image : images) {
            if (image.equals(mthumbs)) {
                image.mThumb = mthumbs.mThumb;
                break;
            }
        }
    }

    private ArrayList<Image> getThumbList() {
        ArrayList<Image> images = new ArrayList<>();
        Cursor data = UIUtils.getContext().getContentResolver().query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{"_data", "image_id"},
                null, null, null);
        if (data != null) {
            try {
                images.clear();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        int id = data.getInt(data
                                .getColumnIndex("image_id"));
                        String path = data.getString(data
                                .getColumnIndex(IMAGE_PROJECTION[0]));
                        Image image = new Image();
                        image.id = id + "";

                        if (path != null) {
                            File file = new File(path);
                            if (file.exists()) {
                                image.mThumb = path;
                                images.add(image);
                            }
                        }
                    } while (data.moveToNext());
                }
                data.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    private void getAll() {
        Cursor data = UIUtils.getContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                null, null, IMAGE_PROJECTION[2]
                        + " DESC");
        if (data != null) {
            try {
                images.clear();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data
                                .getColumnIndex(IMAGE_PROJECTION[0]));
                        String name = data.getString(data
                                .getColumnIndex(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data
                                .getColumnIndex(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data
                                .getColumnIndex(IMAGE_PROJECTION[3]));
                        Image image = new Image(path, name, dateTime);
                        image.id = id + "";
                        images.add(image);
                    } while (data.moveToNext());
                }
                data.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

     List<Image> getAllImages() {
        return images;
    }

     ArrayList<Folder> getAllFolder() {
        return mResultFolder;
    }

}
