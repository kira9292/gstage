import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AssistantgwteService {


  private apiUrl = 'http://localhost:8081/api/stagiaires-proposers'; // Remplacez par votre endpoint réel
  private token = localStorage.getItem('jwtToken'); // ou sessionStorage

  constructor(private http: HttpClient) { }

  postDemande(data: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.token}`
    });

    return this.http.post(this.apiUrl, data, { headers });
  }
}
