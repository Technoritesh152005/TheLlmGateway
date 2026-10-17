package com.llmgateway.admin.apikey;

public class ApiKeyController {
    
}


//we use blocking jdbc cayse jdbc block thread when db query is running and thread wiats or freeze until response is not get
//so we pass a piece of code  in lambda which states execute tjis service in another thread . dont block it