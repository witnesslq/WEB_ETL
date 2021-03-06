package com.arek.services.extractors;


import com.arek.models.RawData;
import com.arek.validator.CodeValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataExtractor implements Extractor {

    @Autowired
    private CodeValidator validator;


    @Override
    public RawData extract(String productCode) throws Exception{
        List<Element> snippets = new ArrayList<>();
        Element pageSnippet;

        if(validator.validate(productCode)){

            pageSnippet = getHtmlSnippet(productCode);
            snippets.add(pageSnippet);
            snippets.addAll(getRestSnippets(pageSnippet));

            return new RawData(snippets, Integer.parseInt(productCode));
        }
        else {
            throw new Exception("Incorrect product number");
        }

    }

    private Element getHtmlSnippet(String nextPage) throws IOException{

        Document document = Jsoup.connect(CENEO_URL+"/"+nextPage)
                .data("query", "Java")
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(10000)
                .get();

        return document.select("#body")
                .first();
    }


    private List<Element> getRestSnippets(Element pageSnippet) throws IOException {
        List<Element> snippet = new ArrayList<>();
        String nextURL;

        while((nextURL = getNext(pageSnippet)) != ""){
            pageSnippet = getHtmlSnippet(nextURL);
            snippet.add(pageSnippet);
        }

        return snippet;
    }

    private String getNext(Element pageSnippet) {
        String nextURL = pageSnippet
                .select(".pagination")
                .select(".page-arrow.arrow-next")
                .select("a")
                .attr("href")
                ;

        return nextURL;
    }

}
