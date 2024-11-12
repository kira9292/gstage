import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DemandeStage } from '../../candidat/stagiaire/interfaces/trainee.interface';

@Injectable({
  providedIn: 'root'
})
export class GwteService {

  private apiUrl = 'http://127.0.0.1:8081/api/stagiaire/documents';

  constructor(private http: HttpClient){}

  getDemandesStage(): Observable<DemandeStage[]> {
    return this.http.get<DemandeStage[]>(`${this.apiUrl}/contrats`);
  }

}
