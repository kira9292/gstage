import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Departement, Service, User} from "../components/dashboard-admin/dashboard-admin.component";
import {Candidat} from "../components/admin-register/admin-register.component";



@Injectable({
  providedIn: 'root'
})
export class AdminServiceService {
  private apiUrl = 'http://localhost:8081/api'; // URL de l'API

  constructor(private http: HttpClient) { }

  // Méthode pour récupérer tous les utilisateurs
  getAppUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl+"/list-users");
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${userId}`);
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${user.id}`, user);
  }
  getServices(): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.apiUrl}/list-services-and-departments`);
  }

  saveUser(payload: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    return this.http.post<any>(this.apiUrl+"/inscription", payload, { headers });
  }
  saveCandiat(payload: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    return this.http.post<any>(this.apiUrl+"/inscription-candidat", payload, { headers });
  }


  createService(service: Service): Observable<Service> {
    return this.http.post<Service>(this.apiUrl+'/depts-services', service);
  }

  deleteService(serviceId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/services/${serviceId}`);
  }


  getDepartement():Observable<Departement[]> {
    return this.http.get<Departement[]>(`${this.apiUrl}/departements`);

  }
  getCandidats(): Observable<Candidat[]> {
    return this.http.get<Candidat[]>(this.apiUrl+"/admin-candidat");
  }

  // createUserFromCandidat(candidat: Candidat): Observable<User1> {
  //   const userData: User1 = {
  //     firstName: candidat.firstName,
  //     lastName: candidat.lastName,
  //     email: candidat.email,
  //     username: `${candidat.firstName.toLowerCase()}.${candidat.lastName.toLowerCase()}`,
  //     roleName: 'STAGIAIRE',
  //     password: ""
  //   };
  //   return this.http.post<User1>(this.apiUrl, userData);
  // }


}
