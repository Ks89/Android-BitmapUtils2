    //new exp version with orientation
    public static Bitmap getNewCombinedByPiecesAlsoGrayscaled(List<Bitmap> bitmapList, int currentStage, int numStages, String direction) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(colorMatrixColorFilter);

        //i mean, don't use greyscale, but add here all the functionalities to reuse the canvas
        switch(direction) {
        
          case "L2R" :
            int originalTotalWidth = bitmapList.get(0).getWidth() * numStages;
            Bitmap finalBitmap = Bitmap.createBitmap(originalTotalWidth, bitmapList.get(0).getHeight(), Bitmap.Config.ARGB_8888);
            float delta = 0f;
            Canvas comboImage = new Canvas(finalBitmap);
            for (int i = 0; i < numStages; i++) {
                comboImage.translate(delta, 0f);
                if (i > currentStage) {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                } else {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                }
                delta = originalTotalWidth / numStages;
            }
            break;  
          case "U2D" :
            int originalTotalHeight = bitmapList.get(0).getHeight() * numStages;
            Bitmap finalBitmap = Bitmap.createBitmap(bitmapList.get(0).getWidth(), originalTotalHeight, Bitmap.Config.ARGB_8888);
            float delta = 0f;
            Canvas comboImage = new Canvas(finalBitmap);
            for (int i = 0; i < numStages; i++) {
                comboImage.translate(0f, delta);
                if (i > currentStage) {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                } else {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                }
                delta = originalTotalHeight / numStages;
            }
            break;
          case "D2U":
            int originalTotalHeight = bitmapList.get(0).getHeight() * numStages;
            Bitmap finalBitmap = Bitmap.createBitmap(bitmapList.get(0).getWidth(), originalTotalHeight, Bitmap.Config.ARGB_8888);
            float delta = 0f;
            Canvas comboImage = new Canvas(finalBitmap);
            for (int i = numStages - 1; i >= 0 ; i--) {
                comboImage.translate(0f, delta);
                if (i > currentStage) {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                } else {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                }
                delta = originalTotalHeight / numStages;
            }
            break;
          case "R2L" :
            int originalTotalWidth = bitmapList.get(0).getWidth() * numStages;
            Bitmap finalBitmap = Bitmap.createBitmap(originalTotalWidth, bitmapList.get(0).getHeight(), Bitmap.Config.ARGB_8888);
            float delta = 0f;
            Canvas comboImage = new Canvas(finalBitmap);
            for (int i = numStages - 1 ; i >= 0 ; i--) {
                comboImage.translate(delta, 0f);
                if (i > currentStage) {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                } else {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                }
                delta = originalTotalWidth / numStages;
            }
            break;
        }
        return finalBitmap;
    }
    
    
    public static Bitmap createTransparentBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }
    
    public static Bitmap createColoredBitmap(int colors[], int offset, int stride, int width, int height, Config config) {
        return Bitmap.createBitmap(colors, offset, stride, width, height, config);
    }
    
  
 
    public static List<Bitmap> splitImageVertically(Bitmap bmpOriginal, int numStages) {
        List<Bitmap> pieces = new ArrayList<>();
        int height = bmpOriginal.getHeight() / numStages;
        int start = 0;
        for (int i = 0; i < numStages; i++) {
            Bitmap pieceBitmap = Bitmap.createBitmap(bmpOriginal, 0 , start, bmpOriginal.getWidth() - 1, height - 1);
            pieces.add(pieceBitmap);
            start = (bmpOriginal.getHeight() / numStages) * (i + 1);
        }
        return pieces;
    }
    
    //efficient way to get a mutable bitmap
    public static Bitmap getMutableBitmap(byte[] data, int offset, int length) {
        return BitmapFactory.decodeByteArray(data, offset, length, BitmapFactory.Options.inMutable);
    }
    public static Bitmap getMutableBitmap(String filePath) {
        return BitmapFactory.decodeFile(data, offset, length, BitmapFactory.Options.inMutable);
    }
    public static Bitmap getMutableBitmap(FileDescriptor fd, Rect outPadding) {
        return BitmapFactory.decodeFileDescriptor(fd, outPadding, BitmapFactory.Options.inMutable);
    }
    public static Bitmap getMutableBitmap(Resources res, int id, Rect outPadding) {
        return BitmapFactory.decodeResource(res, id, BitmapFactory.Options.inMutable);
    }
    public static Bitmap getMutableBitmap(Resources res, TypedValue value, InputStream is, Rect pad) {
        return BitmapFactory.decodeResourceStream(res, value, is, pad, BitmapFactory.Options.inMutable);
    }
    public static Bitmap getMutableBitmap(InputStream is, Rect outPadding) {
        return BitmapFactory.decodeStream(is, outPadding, BitmapFactory.Options.inMutable);
    }
