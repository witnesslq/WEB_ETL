package com.arek.services;

import com.arek.objects.Product;
import com.arek.objects.RawData;

/**
 * Created by Arek on 15.01.2017.
 */
public interface Transformer {
     Product transform(RawData data);
}