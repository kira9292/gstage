import { EducationLevel, InternshipStatus, InternshipType } from "../../../../enums/gstage.enum";
import { ContractStatus, Formation } from "../enums/trainee.enum";


export interface DocumentsCard {
  title: string;
  description: string;
  icon: string;
  count?: number;
  route: string;
  type: string;
}

export interface Contract {
    id: number;
    reference: string;
    startDate: Date;
    endDate: Date;
    compensation: number;
    status: ContractStatus;
    assignmentSite: string;
    signatureDate?: Date;
    comments?: string;
    docs: string;
  }

  export interface AttestationPresence {
    id: number;
    reference: string;
    month: string;
    startDate: Date;
    endDate: Date;
    signatureDate: Date;
    status: boolean;
    comments?: string;
    url: string;
  }

export interface PaginationInfo {
    currentPage: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  }

// interfaces/candidat.interface.ts

export interface Candidat {
  id: number;
  firstName: string;
  lastName: string;
  birthdate?: string;
  nationality?: string;
  birthPlace?: string;
  cni: string;
  address: string;
  email: string;
  phone: string;
  educationalLevel: EducationLevel;
  school: string;
  formation: Formation
}



