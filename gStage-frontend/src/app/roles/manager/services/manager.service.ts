import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManagerService {

  private apiUrl = 'http://localhost:8081/api/manager-internships'; // Remplacez par votre URL réelle

  constructor(private http: HttpClient) { }

  getManagerInternshipRequests(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  validateInternshipRequest(requestId: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${requestId}/validate`, {});
  }
  rejectInternshipRequest(requestId: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${requestId}/reject`, {});
  }

  confirmIntershipStart(requestId: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${requestId}/confirmDebut`, {});
  }

  markInternshipAsEnded(requestId: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${requestId}/markAsEnded`, {});
  }
  sendAttestation(email: string) {
    return this.http.post(`${this.apiUrl}/sendAttestation`, { mail: email });
  }
}
