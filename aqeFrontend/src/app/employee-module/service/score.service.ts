import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { score } from '../models/score';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ScoreService {
  private URLprefix = environment.employeeURLprefix;

  url: string = 'http://localhost:8083/quiz/test/submit';
  constructor(private http: HttpClient) { }

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  postScore(c: number, i: number){
    let scoreJSON:score = {
      correct: c,
      incorrect: i
    };
    return this.http.post<score>(this.URLprefix+'/questions', scoreJSON, this.httpOptions);
  }

  postScore2(testResult){
    
    return this.http.post(this.url, testResult);
  }
}
