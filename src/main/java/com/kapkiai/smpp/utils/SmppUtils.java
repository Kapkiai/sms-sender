package com.kapkiai.smpp.utils;

public class SmppUtils {

    public static int getTotalSegmentsForTextMessage(String message)
    {
        int splitPos = 135;
        int totalsegments = 1;
        if (message.length() > splitPos)
        {
            totalsegments = (message.length() / splitPos) + ((message.length() % splitPos > 0) ? 1 : 0);
        }
        return totalsegments;
    }

    public static String[] splitIntoStringArray(String msg, int pos, int totalSegments)
    {
        String[] segmentData = new String[totalSegments];
        if (totalSegments > 1)
        {
            int splitPos = pos;

            int startIndex = 0;

            segmentData[startIndex] = "";
            segmentData[startIndex] = msg.substring(startIndex, splitPos);

            for (int i = 1; i < totalSegments; i++)
            {
                segmentData[i] = "";
                startIndex = splitPos;
                if (msg.length() - startIndex <= pos)
                {
                    segmentData[i] = msg.substring(startIndex, msg.length());
                }
                else
                {
                    splitPos = startIndex + pos;
                    segmentData[i] = msg.substring(startIndex, splitPos);
                }
            }
        }
        return segmentData;
    }

}
