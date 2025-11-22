# Role-Based Access Control Re-Implementation

## Database Schema Updates
- [x] Add creator_id column to inventory table to track who created each item
- [x] Update DatabaseInitializer with new schema
- [x] Create migration scripts for existing data

## Data Access Layer (DAO) Updates
- [ ] Update InventoryDAO.getAll() to include creator_id
- [ ] Add InventoryDAO.getByTypeAndCreator(type, creatorId) method
- [ ] Add InventoryDAO.getByCreator(creatorId) method
- [ ] Update all CRUD operations to set creator_id

## Model Updates
- [x] Update InventoryItem model to include creatorId field
- [x] Add getters/setters for creatorId
- [x] Update constructors to include creatorId

## Service Layer Updates
- [ ] Create new AccessControlService for RBAC logic
- [ ] Add inventory filtering based on user role and assigned type
- [ ] Implement "own entries only" restriction for staff
- [ ] Add validation methods for staff access

## Controller Layer Updates
- [ ] Completely rewrite InventoryController filtering logic
- [ ] Update loadInventoryData() to use new RBAC methods
- [ ] Update add/edit operations to set creator_id
- [ ] Remove type selection for staff (auto-assign based on assignment)
- [ ] Add proper error handling for unauthorized access

## User Management Updates
- [ ] Ensure UserManagementController properly sets assigned_inventory_type
- [ ] Add validation to prevent staff from accessing other types
- [ ] Update audit logging to track access violations

## Authentication/Session Updates
- [ ] Verify SessionManager correctly stores user role and assignment
- [ ] Add session validation methods

## Testing and Validation
- [ ] Test admin can see all inventory
- [ ] Test club staff only sees/creates club items
- [ ] Test restaurant staff only sees/creates restaurant items
- [ ] Test staff cannot edit items they didn't create
- [ ] Verify audit logging works correctly

## UI/UX Updates
- [ ] Update inventory table to show creator information
- [ ] Add proper error messages for unauthorized access
- [ ] Update filter logic to be seamless for staff users
