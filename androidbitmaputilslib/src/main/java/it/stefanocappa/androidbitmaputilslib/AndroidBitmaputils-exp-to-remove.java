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
            for (int i = 0; i < numStages; i++) {
                comboImage.translate(0f, -delta);
                if (i > currentStage) {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                } else {
                    comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                }
                delta = originalTotalHeight / numStages;
            }
            break;
        }
        return finalBitmap;
    }
