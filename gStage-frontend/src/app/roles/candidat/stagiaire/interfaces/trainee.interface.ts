import { ContractStatus } from "../enums/trainee.enum";

export interface Contract {
    id: number;
    reference: string;
    type: string;
    name: string;
    startDate: Date;
    endDate: Date;
    compensation: number;
    status: ContractStatus;
    assignmentSite: string;
    signatureDate?: Date;
    comments?: string;
    url: string;
  }