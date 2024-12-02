import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { Observable } from 'rxjs';


export interface AttestationRequest {
  startDate: string;
  endDate: string;
  email: string;
}


export interface Manager {
  id: number;
  service: string;
  departement: string;
}

@Injectable({
  providedIn: 'root'
})
export class ManagerService {

  private apiUrl = 'http://localhost:8081/api/manager-internships'; // Remplacez par votre URL réelle

  constructor(private http: HttpClient) { }

  extractManager(): Manager | null {
    try {
      const token  = localStorage.getItem('jwtToken');
      if(token) {
        const decodedToken: any = jwtDecode(token);
          return {
            id: decodedToken.id,
            service: decodedToken.service,
            departement: decodedToken.departement
          };
        }
        return null;
      } catch (error) {
          console.error("Erreur lors du décodage du token:", error);
          return null;
      }
  }
  
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

  sendEndingInternshipAttestation(email: string) {
    return this.http.post(`${this.apiUrl}/sendAttestation`, { mail: email });
  }

  sendPresenceAttestation(request: AttestationRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/send-presence-attestation`, request )
  }
}
