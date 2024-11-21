import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManagerService {

  private apiUrl = 'http://localhost:8080/api/manager-internships'; // Remplacez par votre URL réelle

  constructor(private http: HttpClient) { }

  getManagerInternshipRequests(managerId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/manager/${managerId}`);
  }

  validateInternshipRequest(requestId: string, validate: boolean): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${requestId}/validate`, { 
      validated: validate 
    });
  }
}
