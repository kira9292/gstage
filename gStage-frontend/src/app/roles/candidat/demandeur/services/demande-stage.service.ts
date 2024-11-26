// src/app/demande-stage.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DemandeStageService {

  private apiUrl = 'http://127.0.0.1:8081/api';

  constructor(private http: HttpClient) { }
// Fonction pour soumettre la demande de stage
submitDemandeStage(demandeStageData: any): Observable<any> {
  const headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  // Génération de la référence unique
  const uniqueReference = "REF-DS-" + Math.floor(Math.random() * 10000).toString().padStart(4, '0');

  // Ajout de la référence générée dans les données de la demande
  demandeStageData.demandeStage.reference = uniqueReference;

  // Envoi des données à l'API
  return this.http.post(`${this.apiUrl}/postuler`, demandeStageData, { headers });
}


verifyCode(code: string): Observable<any> {
    const url = `${this.apiUrl}/validerdemande`;
    return this.http.post(url, { code });
  }
}
