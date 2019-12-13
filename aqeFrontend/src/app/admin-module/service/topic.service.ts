import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TopicService {
  private URLprefix = environment.adminURLprefix;
  
  constructor(private http: HttpClient) {  }

  getAllTopics(){
    return this.http.get(this.URLprefix+"/topics");
  }

  deleteTopic(topicName){
    return this.http.get(this.URLprefix+"/topic/delete?name="+topicName,{});
  }

  getAllQuestionsOfTopic(topicName){
    return this.http.get(this.URLprefix+"/topic/"+topicName);
  }
  getTopicMetadata(topicName){
    return this.http.get(this.URLprefix+"/topic/"+topicName+"/metadata").toPromise();
  }
}
