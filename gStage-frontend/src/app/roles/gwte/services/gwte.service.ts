import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GwteService {

  private apiUrl = 'http://127.0.0.1:8081/api';

  constructor(private http: HttpClient){}

  getDemandesStages(): Observable<any[]> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-type', 'application/json');
    return this.http.get<any[]>(`${this.apiUrl}/demandes`, {headers});
  }


  private demandeSource = new BehaviorSubject<any>(null);
  currentDemande = this.demandeSource.asObservable();

  setDemande(demande: any): void {
    this.demandeSource.next(demande);
  }

}
