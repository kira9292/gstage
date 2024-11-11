// src/app/demande-stage.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DemandeStageService {

  private apiUrl = 'http://127.0.0.1:8081/api'; // L'URL de ton API

  constructor(private http: HttpClient) { }

  // Fonction pour soumettre la demande de stage
  submitDemandeStage(demandeStageData: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post(`${this.apiUrl}/postuler`, demandeStageData, { headers });
  }

  verifyCode(code: string): Observable<any> {
    const url = `${this.apiUrl}/validerdemande`;
    return this.http.post(url, { code });
  }
}
